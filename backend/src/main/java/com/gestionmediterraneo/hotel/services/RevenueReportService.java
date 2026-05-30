package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;
import java.time.LocalDate;
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

@Service
public class RevenueReportService {

    private final IInvoiceDAO invoiceDao;

    public RevenueReportService(IInvoiceDAO invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    @Transactional(readOnly = true)
    public RevenueReportResponse calculateRevenue(LocalDate start, LocalDate end, Long roomId, Long clientId, String type) {
        List<Invoice> invoices = invoiceDao.findByFechaEmisionBetweenAndPagadaTrue(start, end);

        if (roomId != null) {
            invoices = invoices.stream()
                .filter(invoice -> invoice.getHabitacion() != null)
                .filter(invoice -> roomId.equals(invoice.getHabitacion().getId()))
                .collect(Collectors.toList());
        }

        if (clientId != null) {
            invoices = invoices.stream()
                .filter(invoice -> invoice.getCliente() != null)
                .filter(invoice -> clientId.equals(invoice.getCliente().getId()))
                .collect(Collectors.toList());
        }

        InvoiceItemType selectedType = null;
        if (type != null && !type.isBlank()) {
            try {
                selectedType = InvoiceItemType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return new RevenueReportResponse(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        }
        final InvoiceItemType typeFilter = selectedType;

        Map<InvoiceItemType, BigDecimal> categoryTotals = new HashMap<>();
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (Invoice invoice : invoices) {
            List<InvoiceItem> items = invoice.getItems();
            boolean hasItems = items != null && !items.isEmpty();

            if (typeFilter != null && hasItems) {
                List<InvoiceItem> matchingItems = items.stream()
                        .filter(item -> item.getType() == typeFilter)
                        .collect(Collectors.toList());

                if (matchingItems.isEmpty()) {
                    continue;
                }

                BigDecimal matchingBase = matchingItems.stream()
                        .map(InvoiceItem::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal invoiceBase = items.stream()
                        .map(InvoiceItem::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal invoiceGross = amountOrZero(invoice.getTotal());
                BigDecimal invoiceNet = amountOrZero(invoice.getSubtotal());
                BigDecimal invoiceTax = amountOrZero(invoice.getIva());

                BigDecimal ratio = invoiceBase.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ONE
                        : matchingBase.divide(invoiceBase, 6, java.math.RoundingMode.HALF_UP);

                BigDecimal gross = invoiceGross.multiply(ratio);
                BigDecimal net = invoiceNet.multiply(ratio);
                BigDecimal tax = invoiceTax.multiply(ratio);

                totalGross = totalGross.add(gross);
                totalNet = totalNet.add(net);
                totalTax = totalTax.add(tax);
                categoryTotals.merge(typeFilter, gross, BigDecimal::add);
                continue;
            }

            if (typeFilter != null) {
                continue;
            }

            BigDecimal gross = amountOrZero(invoice.getTotal());
            BigDecimal net = amountOrZero(invoice.getSubtotal());
            BigDecimal tax = amountOrZero(invoice.getIva());

            totalGross = totalGross.add(gross);
            totalNet = totalNet.add(net);
            totalTax = totalTax.add(tax);

            if (hasItems) {
                BigDecimal invoiceBase = items.stream()
                        .map(InvoiceItem::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                for (InvoiceItem item : items) {
                    BigDecimal itemAmount = amountOrZero(item.getAmount());
                    BigDecimal ratio = invoiceBase.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : itemAmount.divide(invoiceBase, 6, java.math.RoundingMode.HALF_UP);
                    categoryTotals.merge(item.getType(), gross.multiply(ratio), BigDecimal::add);
                }
            } else {
                categoryTotals.merge(InvoiceItemType.HABITACION, gross, BigDecimal::add);
            }
        }

        List<RevenueReportDTO> breakdown = categoryTotals.entrySet().stream()
                .map(entry -> {
                    BigDecimal gross = entry.getValue();
                    BigDecimal net = gross.divide(BigDecimal.valueOf(1.10), 2, java.math.RoundingMode.HALF_UP);
                    BigDecimal tax = gross.subtract(net);
                    return new RevenueReportDTO(entry.getKey().name(), gross, gross, net, tax);
                })
                .collect(Collectors.toList());

        return new RevenueReportResponse(breakdown, totalGross, totalNet, totalTax);
    }

    private BigDecimal amountOrZero(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}
