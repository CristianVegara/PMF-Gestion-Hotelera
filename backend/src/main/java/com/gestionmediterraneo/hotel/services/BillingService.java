package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IChargeDAO;
import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.daos.IInvoiceItemDAO;
import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.entities.Charge;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.entities.InvoiceItem;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.enums.InvoiceItemType;
import com.gestionmediterraneo.hotel.enums.InvoiceStatus;

@Service
public class BillingService {

    private final IInvoiceDAO invoiceDao;
    private final IInvoiceItemDAO invoiceItemDao;
    private final IBookingDAO bookingDao;
    private final IChargeDAO chargeDao;

    public BillingService(IInvoiceDAO invoiceDao, IInvoiceItemDAO invoiceItemDao, IBookingDAO bookingDao, IChargeDAO chargeDao) {
        this.invoiceDao = invoiceDao;
        this.invoiceItemDao = invoiceItemDao;
        this.bookingDao = bookingDao;
        this.chargeDao = chargeDao;
    }

    @Transactional
    public Invoice generateInvoice(InvoiceGenerationRequest request) {
        Booking booking = bookingDao.findById(request.getBookingId()).orElseThrow(() -> new IllegalArgumentException("Booking no encontrado"));

        LocalDate checkIn = booking.getFechaEntrada();
        LocalDate checkOut = booking.getFechaSalida();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Las fechas de reserva no son válidas");
        }

        BigDecimal roomPrice = BigDecimal.valueOf(booking.getHabitacion().getPrice());
        BigDecimal roomSubtotal = roomPrice.multiply(BigDecimal.valueOf(nights));
        BigDecimal subtotal = roomSubtotal;

        List<Charge> charges = chargeDao.findByBookingId(booking.getId());
        List<InvoiceItem> items = new ArrayList<>();

        items.add(buildItem(InvoiceItemType.HABITACION, "Habitación " + booking.getHabitacion().getNumber(), (int) nights, roomPrice, roomSubtotal));

        for (Charge charge : charges) {
            BigDecimal amount = charge.getAmount();
            subtotal = subtotal.add(amount);
            items.add(buildItem(mapChargeType(charge.getType()), charge.getDescription(), 1, amount, amount));
            charge.setAppliedToInvoice(true);
        }

        BigDecimal discountPct = request.getDiscountPercentage() == null ? BigDecimal.ZERO : request.getDiscountPercentage();
        BigDecimal discountAmount = subtotal.multiply(discountPct).divide(BigDecimal.valueOf(100));
        BigDecimal subtotalAfterDiscount = subtotal.subtract(discountAmount);
        BigDecimal taxPct = request.getTaxPercentage() == null ? BigDecimal.ZERO : request.getTaxPercentage();
        BigDecimal taxAmount = subtotalAfterDiscount.multiply(taxPct).divide(BigDecimal.valueOf(100));
        BigDecimal total = subtotalAfterDiscount.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setCliente(booking.getCliente());
        invoice.setHabitacion(booking.getHabitacion());
        invoice.setBooking(booking);
        invoice.setStatus(InvoiceStatus.PENDIENTE);
        invoice.setFechaEmision(LocalDate.now());
        invoice.setConcepto(request.getMemo() == null ? "Factura generada para reserva" : request.getMemo());
        invoice.setNoches((int) nights);
        invoice.setPrecio(roomPrice);
        invoice.setSubtotalBeforeDiscount(subtotal);
        invoice.setDiscountPercentage(discountPct);
        invoice.setDiscountAmount(discountAmount);
        invoice.setSubtotal(subtotalAfterDiscount);
        invoice.setIva(taxAmount);
        invoice.setTotal(total);
        invoice.setPagada(false);
        invoice.setItems(items);

        for (InvoiceItem item : items) {
            item.setInvoice(invoice);
        }

        Invoice saved = invoiceDao.save(invoice);
        chargeDao.saveAll(charges);

        return saved;
    }

    private InvoiceItem buildItem(InvoiceItemType type, String description, int quantity, BigDecimal unitPrice, BigDecimal amount) {
        InvoiceItem item = new InvoiceItem();
        item.setType(type);
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setAmount(amount);
        return item;
    }

    private InvoiceItemType mapChargeType(com.gestionmediterraneo.hotel.enums.ChargeType chargeType) {
        return switch (chargeType) {
            case MINIBAR -> InvoiceItemType.MINIBAR;
            case LIMPIEZA -> InvoiceItemType.LIMPIEZA;
            case ACTIVIDAD -> InvoiceItemType.ACTIVIDAD;
            case EXTRA -> InvoiceItemType.EXTRA;
            case HABITACION -> InvoiceItemType.HABITACION;
        };
    }
}
