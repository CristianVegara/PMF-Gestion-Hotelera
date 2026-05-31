package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.entities.InvoiceItem;
import com.gestionmediterraneo.hotel.enums.InvoiceItemType;

/**
 * Service for calculating revenue reports from paid invoices.
 *
 * <p>Revenue is attributed to the booking stay period, not the invoice
 * emission date. Supports optional filters by room, client, and item type.</p>
 *
 * @author Gestión Mediterráneo
 */
@Service
public class RevenueReportService {

    /** Invoice DAO. */
    private final IInvoiceDAO invoiceDao;

    /**
     * Constructor for RevenueReportService.
     * @param invoiceDao invoice data access object
     */
    public RevenueReportService(IInvoiceDAO invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    /**
     * Calculates revenue for paid invoices whose booking stay overlaps
     * [start, end]. Optional filters: roomId, clientId, item type.
     *
     * <p>Revenue is attributed to the booking stay period, not the invoice
     * emission date, so a booking that ran 28 Apr – 3 May will appear in a
     * "May" report even if the invoice was emitted on 30 Apr.</p>
     *
     * @return Map with keys: breakdown (List of category maps), totalGross,
     *         totalNet, totalTax
     */
    @Transactional(readOnly = true)
    public Map<String, Object> calculateRevenue(
            LocalDate start,
            LocalDate end,
            Long roomId,
            Long clientId,
            String type) {

        List<Invoice> invoices = invoiceDao.findPaidByBookingDateRange(start, end);

        if (roomId != null) {
            invoices = invoices.stream()
                    .filter(i -> i.getHabitacion() != null && roomId.equals(i.getHabitacion().getId()))
                    .collect(Collectors.toList());
        }

        if (clientId != null) {
            invoices = invoices.stream()
                    .filter(i -> i.getCliente() != null && clientId.equals(i.getCliente().getId()))
                    .collect(Collectors.toList());
        }

        final InvoiceItemType typeFilter = resolveTypeFilter(type);
        if (typeFilter == null && type != null && !type.isBlank()) {
            return buildResult(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Map<InvoiceItemType, BigDecimal> categoryGross = new HashMap<>();
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalNet   = BigDecimal.ZERO;
        BigDecimal totalTax   = BigDecimal.ZERO;

        for (Invoice invoice : invoices) {
            List<InvoiceItem> items    = invoice.getItems();
            boolean           hasItems = items != null && !items.isEmpty();

            if (typeFilter != null) {
                if (!hasItems) continue;

                List<InvoiceItem> matching = items.stream()
                        .filter(item -> item.getType() == typeFilter)
                        .collect(Collectors.toList());

                if (matching.isEmpty()) continue;

                BigDecimal invoiceBase  = sumAmounts(items);
                BigDecimal matchingBase = sumAmounts(matching);
                BigDecimal ratio        = ratio(matchingBase, invoiceBase);

                BigDecimal gross = safeAmount(invoice.getTotal()).multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal net   = safeAmount(invoice.getSubtotal()).multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                BigDecimal tax   = safeAmount(invoice.getIva()).multiply(ratio).setScale(2, RoundingMode.HALF_UP);

                totalGross = totalGross.add(gross);
                totalNet   = totalNet.add(net);
                totalTax   = totalTax.add(tax);
                categoryGross.merge(typeFilter, gross, BigDecimal::add);

            } else {
                BigDecimal gross = safeAmount(invoice.getTotal());
                BigDecimal net   = safeAmount(invoice.getSubtotal());
                BigDecimal tax   = safeAmount(invoice.getIva());

                totalGross = totalGross.add(gross);
                totalNet   = totalNet.add(net);
                totalTax   = totalTax.add(tax);

                if (hasItems) {
                    BigDecimal invoiceBase = sumAmounts(items);
                    for (InvoiceItem item : items) {
                        BigDecimal itemRatio = ratio(safeAmount(item.getAmount()), invoiceBase);
                        categoryGross.merge(
                                item.getType(),
                                gross.multiply(itemRatio).setScale(2, RoundingMode.HALF_UP),
                                BigDecimal::add);
                    }
                } else {
                    categoryGross.merge(InvoiceItemType.HABITACION, gross, BigDecimal::add);
                }
            }
        }

        return buildResult(buildBreakdown(categoryGross), totalGross, totalNet, totalTax);
    }
    private InvoiceItemType resolveTypeFilter(String type) {
        if (type == null || type.isBlank()) return null;
        try {
            return InvoiceItemType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BigDecimal sumAmounts(List<InvoiceItem> items) {
        return items.stream()
                .map(InvoiceItem::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
        if (denominator.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ONE;
        return numerator.divide(denominator, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private List<Map<String, Object>> buildBreakdown(Map<InvoiceItemType, BigDecimal> categoryGross) {
        List<Map<String, Object>> breakdown = new ArrayList<>();
        for (Map.Entry<InvoiceItemType, BigDecimal> entry : categoryGross.entrySet()) {
            BigDecimal gross = entry.getValue();
            BigDecimal net   = gross.divide(BigDecimal.valueOf(1.10), 2, RoundingMode.HALF_UP);
            BigDecimal tax   = gross.subtract(net);

            Map<String, Object> category = new HashMap<>();
            category.put("category",    entry.getKey().name());
            category.put("totalGross",  gross);
            category.put("totalNet",    net);
            category.put("totalTax",    tax);
            category.put("totalAmount", gross);
            breakdown.add(category);
        }
        return breakdown;
    }

    private Map<String, Object> buildResult(
            List<Map<String, Object>> breakdown,
            BigDecimal totalGross,
            BigDecimal totalNet,
            BigDecimal totalTax) {

        Map<String, Object> result = new HashMap<>();
        result.put("breakdown",  breakdown);
        result.put("totalGross", totalGross);
        result.put("totalNet",   totalNet);
        result.put("totalTax",   totalTax);
        return result;
    }
}
