package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
    void shouldFindAll() {
        List<Invoice> invoices = Arrays.asList(new Invoice(), new Invoice());
        when(invoiceDao.findAll()).thenReturn(invoices);

        List<Invoice> result = invoiceService.findAll();

        assertEquals(2, result.size());
        verify(invoiceDao, times(1)).findAll();
    }

    @Test
    void shouldFindAllSortedAsc() {
        List<Invoice> invoices = Arrays.asList(new Invoice());
        when(invoiceDao.findAll(any(Sort.class))).thenReturn(invoices);

        List<Invoice> result = invoiceService.findAllSorted("id", "asc");

        assertEquals(1, result.size());
        verify(invoiceDao).findAll(any(Sort.class));
    }

    @Test
    void shouldFindAllSortedDesc() {
        List<Invoice> invoices = Arrays.asList(new Invoice());
        when(invoiceDao.findAll(any(Sort.class))).thenReturn(invoices);

        List<Invoice> result = invoiceService.findAllSorted("id", "desc");

        assertEquals(1, result.size());
        verify(invoiceDao).findAll(any(Sort.class));
    }

    @Test
    void shouldFindById() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        when(invoiceDao.findById(1L)).thenReturn(Optional.of(invoice));

        Optional<Invoice> result = invoiceService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {
        when(invoiceDao.findById(1L)).thenReturn(Optional.empty());

        Optional<Invoice> result = invoiceService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindByDateRange() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);
        List<Invoice> invoices = Arrays.asList(new Invoice());
        when(invoiceDao.findByFechaEmisionBetween(start, end)).thenReturn(invoices);

        List<Invoice> result = invoiceService.findByDateRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByClientBookingDateRange() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);
        List<Invoice> invoices = Arrays.asList(new Invoice());
        when(invoiceDao.findByClienteIdAndBookingDateRange(1L, start, end)).thenReturn(invoices);

        List<Invoice> result = invoiceService.findByClientBookingDateRange(1L, start, end);

        assertEquals(1, result.size());
    }

    @Test
    void shouldSave() {
        Invoice invoice = new Invoice();
        when(invoiceDao.save(invoice)).thenReturn(invoice);

        Invoice result = invoiceService.save(invoice);

        assertNotNull(result);
        verify(invoiceDao).save(invoice);
    }

    @Test
    void shouldDelete() {
        invoiceService.delete(1L);
        verify(invoiceDao).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        doThrow(new RuntimeException("DB error")).when(invoiceDao).deleteById(1L);

        assertThrows(RuntimeException.class, () -> invoiceService.delete(1L));
    }
}