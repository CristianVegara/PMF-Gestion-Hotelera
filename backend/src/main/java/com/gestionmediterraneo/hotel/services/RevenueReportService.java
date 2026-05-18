package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IInvoiceItemDAO;
import com.gestionmediterraneo.hotel.entities.InvoiceItem;
import com.gestionmediterraneo.hotel.enums.InvoiceItemType;

@Service
public class RevenueReportService {

    private final IInvoiceItemDAO invoiceItemDao;

    public RevenueReportService(IInvoiceItemDAO invoiceItemDao) {
        this.invoiceItemDao = invoiceItemDao;
    }

    @Transactional(readOnly = true)
    public RevenueReportResponse calculateRevenue(LocalDate start, LocalDate end, Long roomId, Long clientId, String type) {
        List<InvoiceItem> items = invoiceItemDao.findByInvoice_FechaEmisionBetween(start, end);

        if (roomId != null) {
            items = items.stream()
                .filter(item -> item.getInvoice().getBooking() != null && item.getInvoice().getBooking().getHabitacion() != null)
                .filter(item -> roomId.equals(item.getInvoice().getBooking().getHabitacion().getId()))
                .collect(Collectors.toList());
        }

        if (clientId != null) {
            items = items.stream()
                .filter(item -> item.getInvoice().getCliente() != null)
                .filter(item -> clientId.equals(item.getInvoice().getCliente().getId()))
                .collect(Collectors.toList());
        }

        if (type != null && !type.isBlank()) {
            try {
                InvoiceItemType itemType = InvoiceItemType.valueOf(type.toUpperCase());
                items = items.stream()
                    .filter(item -> item.getType() == itemType)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                items = new ArrayList<>();
            }
        }

        Map<InvoiceItemType, BigDecimal> categoryTotals = new HashMap<>();
        BigDecimal gross = BigDecimal.ZERO;
        BigDecimal net = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;

        for (InvoiceItem item : items) {
            BigDecimal amount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
            gross = gross.add(amount);
            if (item.getInvoice() != null && item.getInvoice().getIva() != null) {
                tax = tax.add(item.getInvoice().getIva());
            }
            categoryTotals.merge(item.getType(), amount, BigDecimal::add);
        }

        net = gross.subtract(tax);

        List<RevenueReportDTO> breakdown = categoryTotals.entrySet().stream()
                .map(entry -> new RevenueReportDTO(entry.getKey().name(), entry.getValue(), entry.getValue(), entry.getValue().subtract(itemTax(entry.getKey(), entry.getValue())), itemTax(entry.getKey(), entry.getValue())))
                .collect(Collectors.toList());

        return new RevenueReportResponse(breakdown, gross, net, tax);
    }

    private BigDecimal itemTax(InvoiceItemType type, BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.10));
    }
}
