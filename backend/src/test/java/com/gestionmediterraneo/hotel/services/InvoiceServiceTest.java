package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.entities.Invoice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private IInvoiceDAO invoiceDao;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void shouldReturnAllInvoices() {
        List<Invoice> invoices = Arrays.asList(new Invoice(), new Invoice());

        when(invoiceDao.findAll()).thenReturn(invoices);

        List<Invoice> result = invoiceService.findAll();

        assertEquals(2, result.size());
        verify(invoiceDao, times(1)).findAll();
    }

    @Test
    void shouldReturnSortedInvoicesAsc() {
        List<Invoice> invoices = Arrays.asList(new Invoice());

        when(invoiceDao.findAll(any(Sort.class))).thenReturn(invoices);

        List<Invoice> result = invoiceService.findAllSorted("id", "asc");

        assertEquals(1, result.size());
        verify(invoiceDao).findAll(any(Sort.class));
    }

    @Test
    void shouldReturnSortedInvoicesDesc() {
        List<Invoice> invoices = Arrays.asList(new Invoice());

        when(invoiceDao.findAll(any(Sort.class))).thenReturn(invoices);

        List<Invoice> result = invoiceService.findAllSorted("id", "desc");

        assertEquals(1, result.size());
        verify(invoiceDao).findAll(any(Sort.class));
    }

    @Test
    void shouldReturnInvoiceWhenIdExists() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(invoiceDao.findById(1L)).thenReturn(Optional.of(invoice));

        Optional<Invoice> result = invoiceService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenInvoiceDoesNotExist() {
        when(invoiceDao.findById(1L)).thenReturn(Optional.empty());

        Optional<Invoice> result = invoiceService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldSaveInvoice() {
        Invoice invoice = new Invoice();
        invoice.setTotal(new BigDecimal("100.00"));

        when(invoiceDao.save(any(Invoice.class))).thenReturn(invoice);

        Invoice result = invoiceService.save(invoice);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getTotal());
        verify(invoiceDao).save(invoice);
    }

    @Test
    void shouldSaveInvoiceWithExistingDiscountFields() {
        Invoice invoice = new Invoice();
        invoice.setSubtotalBeforeDiscount(new BigDecimal("100.00"));
        invoice.setDiscountPercentage(new BigDecimal("5.00"));
        invoice.setDiscountAmount(new BigDecimal("5.00"));
        invoice.setSubtotal(new BigDecimal("95.00"));
        invoice.setIva(new BigDecimal("9.50"));
        invoice.setTotal(new BigDecimal("104.50"));

        when(invoiceDao.save(any(Invoice.class))).thenReturn(invoice);

        Invoice result = invoiceService.save(invoice);

        assertEquals(new BigDecimal("5.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("104.50"), result.getTotal());
        verify(invoiceDao).save(invoice);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Invoice invoice = new Invoice();
        invoice.setTotal(new BigDecimal("100.00"));

        when(invoiceDao.save(any(Invoice.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            invoiceService.save(invoice);
        });
    }

    @Test
    void shouldDeleteInvoice() {
        Long id = 1L;

        invoiceService.delete(id);

        verify(invoiceDao, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        Long id = 1L;

        doThrow(new RuntimeException("DB error")).when(invoiceDao).deleteById(id);

        assertThrows(RuntimeException.class, () -> {
            invoiceService.delete(id);
        });
    }
}
