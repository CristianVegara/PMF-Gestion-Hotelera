package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IChargeDAO;
import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.InvoiceStatus;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private IInvoiceDAO invoiceDao;

    @Mock
    private IBookingDAO bookingDao;

    @Mock
    private IChargeDAO chargeDao;

    @Mock
    private IRoomDAO roomDao;

    @Mock
    private LoyaltyService loyaltyService;

    @Test
    void shouldCreatePendingInvoiceFromBooking() {
        Client client = new Client();
        client.setId(1L);

        Room room = new Room();
        room.setId(1L);
        room.setNumber("101");
        room.setPrice(100.0);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCliente(client);
        booking.setHabitacion(room);
        booking.setFechaEntrada(LocalDate.of(2026, 1, 10));
        booking.setFechaSalida(LocalDate.of(2026, 1, 12));

        when(invoiceDao.findByBooking_Id(1L)).thenReturn(List.of());
        when(chargeDao.findByBookingId(1L)).thenReturn(List.of());
        when(loyaltyService.calculateTier(client)).thenReturn(new LoyaltyTier("BRONZE", 0, 0, 0));
        when(invoiceDao.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BillingService service = new BillingService(invoiceDao, bookingDao, chargeDao, roomDao, loyaltyService);
        Invoice invoice = service.createPendingInvoiceForBooking(booking);

        assertFalse(invoice.isPagada());
        assertEquals(InvoiceStatus.PENDIENTE, invoice.getStatus());
        assertTrue(new BigDecimal("200.0").compareTo(invoice.getSubtotalBeforeDiscount()) == 0);
        assertTrue(new BigDecimal("220.00").compareTo(invoice.getTotal()) == 0);
        assertEquals(1, invoice.getItems().size());
    }

    @Test
    void shouldMarkBookingInvoicesPaid() {
        Invoice invoice = new Invoice();
        invoice.setPagada(false);
        invoice.setStatus(InvoiceStatus.PENDIENTE);

        when(invoiceDao.findByBooking_Id(1L)).thenReturn(List.of(invoice));

        BillingService service = new BillingService(invoiceDao, bookingDao, chargeDao, roomDao, loyaltyService);
        service.markBookingInvoicesPaid(1L);

        assertTrue(invoice.isPagada());
        assertEquals(InvoiceStatus.PAGADA, invoice.getStatus());
        verify(invoiceDao).saveAll(List.of(invoice));
    }
}
