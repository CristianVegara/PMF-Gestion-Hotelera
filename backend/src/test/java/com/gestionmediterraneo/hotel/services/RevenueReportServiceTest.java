package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.entities.Invoice;

@ExtendWith(MockitoExtension.class)
class RevenueReportServiceTest {

    @Mock
    private IInvoiceDAO invoiceDao;

    @Test
    void shouldCalculateRevenueOnlyFromPaidInvoicesInDateRange() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        Invoice invoice = new Invoice();
        invoice.setTotal(new BigDecimal("121.00"));
        invoice.setSubtotal(new BigDecimal("110.00"));
        invoice.setIva(new BigDecimal("11.00"));
        invoice.setPagada(true);

        when(invoiceDao.findByFechaEmisionBetweenAndPagadaTrue(from, to)).thenReturn(List.of(invoice));

        RevenueReportService service = new RevenueReportService(invoiceDao);
        RevenueReportResponse response = service.calculateRevenue(from, to, null, null, null);

        assertTrue(new BigDecimal("121.00").compareTo(response.getTotalGross()) == 0);
        assertTrue(new BigDecimal("110.00").compareTo(response.getTotalNet()) == 0);
        assertTrue(new BigDecimal("11.00").compareTo(response.getTotalTax()) == 0);
        assertEquals("HABITACION", response.getBreakdown().get(0).getCategory());
    }
}
