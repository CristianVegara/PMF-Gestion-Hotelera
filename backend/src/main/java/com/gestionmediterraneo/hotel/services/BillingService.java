package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IChargeDAO;
import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Charge;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.entities.InvoiceItem;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.InvoiceItemType;
import com.gestionmediterraneo.hotel.enums.InvoiceStatus;

/**
 * Service for generating and managing hotel invoices.
 *
 * <p>Handles invoice building logic including room charges,
 * line items, discounts (including loyalty-based), and tax calculation.</p>
 *
 * @author Gestión Mediterráneo
 * @see Invoice
 * @see BillingService
 */
@Service
public class BillingService {

    private final IInvoiceDAO    invoiceDao;
    private final IBookingDAO    bookingDao;
    private final IChargeDAO     chargeDao;
    private final IRoomDAO       roomDao;
    private final LoyaltyService loyaltyService;

    /**
     * Constructor for BillingService.
     * @param invoiceDao     invoice data access object
     * @param bookingDao     booking data access object
     * @param chargeDao      charge data access object
     * @param roomDao        room data access object
     * @param loyaltyService loyalty tier calculation service
     */
    public BillingService(
            IInvoiceDAO invoiceDao,
            IBookingDAO bookingDao,
            IChargeDAO chargeDao,
            IRoomDAO roomDao,
            LoyaltyService loyaltyService) {
        this.invoiceDao    = invoiceDao;
        this.bookingDao    = bookingDao;
        this.chargeDao     = chargeDao;
        this.roomDao       = roomDao;
        this.loyaltyService = loyaltyService;
    }
    /**
     * Generates (or regenerates) a full invoice from an
     * {@link InvoiceGenerationRequest}.  Called explicitly by staff.
     * @param request the invoice generation request
     * @return the generated invoice
     */
    @Transactional
    public Invoice generateInvoice(InvoiceGenerationRequest request) {
        Booking booking = bookingDao.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking no encontrado"));
        return buildOrRefreshInvoice(
                booking,
                request.getTaxPercentage(),
                request.getDiscountPercentage(),
                request.getMemo(),
                false);
    }

    /**
     * Creates a pending invoice for a booking.
     * @param booking the booking to create the invoice for
     * @return the created pending invoice
     */
    @Transactional
    public Invoice createPendingInvoiceForBooking(Booking booking) {
        return buildOrRefreshInvoice(
                booking,
                BigDecimal.valueOf(10),
                null,
                "Reserva pendiente de abono",
                true);
    }

    /**
     * Marks all invoices associated with a booking as paid.
     * @param bookingId the booking identifier
     */
    @Transactional
    public void markBookingInvoicesPaid(Long bookingId) {
        List<Invoice> invoices = invoiceDao.findByBooking_Id(bookingId);
        for (Invoice invoice : invoices) {
            invoice.setPagada(true);
            invoice.setStatus(InvoiceStatus.PAGADA);
        }
        invoiceDao.saveAll(invoices);
    }
    /**
     * Single method that builds or refreshes an invoice.
     *
     * @param booking                 the booking to invoice
     * @param taxPercentage           tax percentage to apply
     * @param requestedDiscountPercentage  requested discount percentage (null for loyalty-based)
     * @param memo                    memo/concept for the invoice
     * @param isPending               whether this is a pending invoice
     * @return the built or refreshed invoice
     */
    private Invoice buildOrRefreshInvoice(
            Booking booking,
            BigDecimal taxPercentage,
            BigDecimal requestedDiscountPercentage,
            String memo,
            boolean isPending) {

        long nights = resolveNights(booking);
        Room billingRoom = resolveRoom(booking);

        // --- room line ---
        BigDecimal roomPrice    = BigDecimal.valueOf(billingRoom.getPrice());
        BigDecimal roomSubtotal = roomPrice.multiply(BigDecimal.valueOf(nights));

        // --- charges ---
        List<Charge>      charges     = chargeDao.findByBookingId(booking.getId());
        List<InvoiceItem> items       = new ArrayList<>();
        BigDecimal        chargesTotal = BigDecimal.ZERO;

        String roomDescription = booking.getHabitacion() != null
                ? "Habitación " + booking.getHabitacion().getNumber()
                : "Habitación " + booking.getRoomType();
        items.add(buildItem(InvoiceItemType.HABITACION, roomDescription, (int) nights, roomPrice, roomSubtotal));

        for (Charge charge : charges) {
            BigDecimal amount = charge.getAmount();
            chargesTotal = chargesTotal.add(amount);
            items.add(buildItem(mapChargeType(charge.getType()), charge.getDescription(), 1, amount, amount));
            if (!isPending) {
                charge.setAppliedToInvoice(true);
            }
        }

        BigDecimal subtotalBeforeDiscount = roomSubtotal.add(chargesTotal);

        BigDecimal discountPct = requestedDiscountPercentage;
        String     loyaltyRank = null;
        if (discountPct == null && booking.getCliente() != null) {
            LoyaltyTier tier = loyaltyService.calculateTier(booking.getCliente());
            discountPct  = BigDecimal.valueOf(tier.getDiscountPercentage());
            loyaltyRank  = tier.getRank();
        }
        if (discountPct == null) discountPct = BigDecimal.ZERO;

        BigDecimal discountAmount        = subtotalBeforeDiscount
                .multiply(discountPct)
                .divide(BigDecimal.valueOf(100));
        BigDecimal subtotalAfterDiscount = subtotalBeforeDiscount.subtract(discountAmount);
        BigDecimal taxPct                = taxPercentage != null ? taxPercentage : BigDecimal.ZERO;
        BigDecimal taxAmount             = subtotalAfterDiscount
                .multiply(taxPct)
                .divide(BigDecimal.valueOf(100));
        BigDecimal total                 = subtotalAfterDiscount.add(taxAmount);

        Invoice invoice = invoiceDao.findByBooking_Id(booking.getId())
                .stream()
                .findFirst()
                .orElseGet(Invoice::new);

        invoice.setCliente(booking.getCliente());
        invoice.setHabitacion(booking.getHabitacion());
        invoice.setBooking(booking);
        invoice.setStatus(isPending ? InvoiceStatus.PENDIENTE : InvoiceStatus.PENDIENTE);
        invoice.setFechaEmision(invoice.getFechaEmision() != null ? invoice.getFechaEmision() : LocalDate.now());
        invoice.setConcepto(memo == null || memo.isBlank() ? "Factura generada para reserva" : memo);
        invoice.setNoches((int) nights);
        invoice.setPrecio(roomPrice);
        invoice.setSubtotalBeforeDiscount(subtotalBeforeDiscount);
        invoice.setDiscountPercentage(discountPct);
        invoice.setDiscountAmount(discountAmount);
        invoice.setLoyaltyRank(loyaltyRank);
        invoice.setSubtotal(subtotalAfterDiscount);
        invoice.setIva(taxAmount);
        invoice.setTotal(total);
        invoice.setPagada(false);

        invoice.getItems().clear();
        invoice.getItems().addAll(items);
        for (InvoiceItem item : items) {
            item.setInvoice(invoice);
        }

        Invoice saved = invoiceDao.save(invoice);

        if (!isPending) {
            chargeDao.saveAll(charges);
        }

        return saved;
    }
    /** Resolve the number of nights for a booking. */
    private long resolveNights(Booking booking) {
        LocalDate checkIn  = booking.getFechaEntrada();
        LocalDate checkOut = booking.getFechaSalida();
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("La reserva no tiene fechas válidas");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Las fechas de reserva no son válidas");
        }
        return nights;
    }

    /** Resolve the room to use for billing. */
    private Room resolveRoom(Booking booking) {
        if (booking.getHabitacion() != null) return booking.getHabitacion();
        if (booking.getRoomType() != null) {
            return roomDao.findFirstByType(booking.getRoomType())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se encontró habitación del tipo " + booking.getRoomType()));
        }
        throw new IllegalArgumentException(
                "La reserva debe tener una habitación o tipo de habitación para generar la factura");
    }

    /** Build an invoice item from the given parameters. */
    private InvoiceItem buildItem(
            InvoiceItemType type,
            String description,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal amount) {
        InvoiceItem item = new InvoiceItem();
        item.setType(type);
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setAmount(amount);
        return item;
    }

    /** Map a {@link ChargeType} to an {@link InvoiceItemType}. */
    private InvoiceItemType mapChargeType(com.gestionmediterraneo.hotel.enums.ChargeType chargeType) {
        return switch (chargeType) {
            case MINIBAR    -> InvoiceItemType.MINIBAR;
            case LIMPIEZA   -> InvoiceItemType.LIMPIEZA;
            case ACTIVIDAD  -> InvoiceItemType.ACTIVIDAD;
            case EXTRA      -> InvoiceItemType.EXTRA;
            case HABITACION -> InvoiceItemType.HABITACION;
        };
    }
}
