package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IChargeDAO;
import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.daos.IInvoiceItemDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.entities.Invoice;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private IInvoiceDAO invoiceDao;

    @Mock
    private IInvoiceItemDAO invoiceItemDao;

    @Mock
    private IBookingDAO bookingDao;

    @Mock
    private IChargeDAO chargeDao;

    @InjectMocks
    private BillingService billingService;

    @Test
    void shouldGenerateInvoiceSuccessfully() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setFechaEntrada(LocalDate.now());
        booking.setFechaSalida(LocalDate.now().plusDays(2));
        
        Room habitacion = new Room();
        habitacion.setPrice(100.0);
        habitacion.setNumber("101");
        booking.setHabitacion(habitacion);

        InvoiceGenerationRequest request = new InvoiceGenerationRequest();
        request.setBookingId(bookingId);
        request.setDiscountPercentage(BigDecimal.TEN);
        request.setTaxPercentage(BigDecimal.valueOf(21));

        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));
        when(chargeDao.findByBookingId(bookingId)).thenReturn(Collections.emptyList());
        when(invoiceDao.save(any(Invoice.class))).thenAnswer(i -> i.getArguments()[0]);

        Invoice result = billingService.generateInvoice(request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200.0), result.getSubtotalBeforeDiscount());
        verify(chargeDao).saveAll(anyIterable());
        verify(invoiceDao).save(any(Invoice.class));
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        InvoiceGenerationRequest request = new InvoiceGenerationRequest();
        request.setBookingId(99L);
        when(bookingDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> billingService.generateInvoice(request));
    }

    @Test
    void shouldThrowExceptionWhenDatesAreInvalid() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setFechaEntrada(LocalDate.now());
        booking.setFechaSalida(LocalDate.now());
        
        InvoiceGenerationRequest request = new InvoiceGenerationRequest();
        request.setBookingId(bookingId);
        
        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> billingService.generateInvoice(request));
    }
}