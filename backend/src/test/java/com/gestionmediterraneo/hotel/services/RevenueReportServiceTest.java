package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
        LocalDate to   = LocalDate.of(2026, 1, 31);

        Invoice invoice = new Invoice();
        invoice.setTotal(new BigDecimal("121.00"));
        invoice.setSubtotal(new BigDecimal("110.00"));
        invoice.setIva(new BigDecimal("11.00"));
        invoice.setPagada(true);

        when(invoiceDao.findPaidByBookingDateRange(from, to)).thenReturn(List.of(invoice));

        RevenueReportService service = new RevenueReportService(invoiceDao);
        Map<String, Object> response = service.calculateRevenue(from, to, null, null, null);

        assertNotNull(response);
        assertEquals(new BigDecimal("121.00"), response.get("totalGross"));
        assertEquals(new BigDecimal("110.00"), response.get("totalNet"));
        assertEquals(new BigDecimal("11.00"),  response.get("totalTax"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> breakdown = (List<Map<String, Object>>) response.get("breakdown");
        assertNotNull(breakdown);
        assertEquals(1, breakdown.size());

        Map<String, Object> habitacionEntry = breakdown.get(0);
        assertEquals("HABITACION", habitacionEntry.get("category"));

        BigDecimal expectedBreakdownGross = new BigDecimal("121.00");
        BigDecimal expectedBreakdownNet   = expectedBreakdownGross
                .divide(new BigDecimal("1.10"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal expectedBreakdownTax   = expectedBreakdownGross.subtract(expectedBreakdownNet);

        assertEquals(expectedBreakdownGross, habitacionEntry.get("totalGross"));
        assertEquals(expectedBreakdownNet,   habitacionEntry.get("totalNet"));
        assertEquals(expectedBreakdownTax,   habitacionEntry.get("totalTax"));
    }
}