package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.InvoiceStatus;

/**
 * Represents a hotel invoice for a booking or client.
 *
 * <p>An invoice contains billing details including room charges,
 * discounts, taxes (IVA), and line items. It is linked to a
 * {@link Client}, optionally a {@link Booking}, and a {@link Room}.</p>
 *
 * @author Gestión Mediterráneo
 * @see InvoiceItem
 * @see Payment
 * @see InvoiceStatus
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    /** Unique identifier for the invoice. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Client to whom this invoice belongs. */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client cliente;

    /** Associated booking (nullable for non-booking invoices). */
    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonIgnoreProperties({"charges", "cliente"})
    private Booking booking;

    /** Room associated with this invoice. */
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room habitacion;

    /** Current status of the invoice. */
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    /** Date the invoice was issued. */
    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    /** Line items on this invoice. */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"invoice"})
    private List<InvoiceItem> items = new ArrayList<>();

    /** Payments made against this invoice. */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"invoice"})
    private List<Payment> payments = new ArrayList<>();

    /** Concept/description of the invoice. */
    private String concepto;
    /** Number of nights billed. */
    private int noches;
    /** Base price per night. */
    private BigDecimal precio;
    /** Subtotal before any discount is applied. */
    private BigDecimal subtotalBeforeDiscount;
    /** Subtotal after discount. */
    private BigDecimal subtotal;
    /** Discount percentage applied. */
    private BigDecimal discountPercentage;
    /** Discount amount. */
    private BigDecimal discountAmount;
    /** Loyalty tier rank used for discount calculation. */
    private String loyaltyRank;
    /** IVA (VAT) tax amount. */
    private BigDecimal iva;
    /** Total amount due. */
    private BigDecimal total;
    /** Whether the invoice has been paid. */
    private boolean pagada;

    @PrePersist
    public void prePersist() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDate.now();
        }
        if (this.status == null) {
            this.status = InvoiceStatus.PENDIENTE;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getCliente() { return cliente; }
    public void setCliente(Client cliente) { this.cliente = cliente; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Room getHabitacion() { return habitacion; }
    public void setHabitacion(Room habitacion) { this.habitacion = habitacion; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public int getNoches() { return noches; }
    public void setNoches(int noches) { this.noches = noches; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public BigDecimal getSubtotalBeforeDiscount() { return subtotalBeforeDiscount; }
    public void setSubtotalBeforeDiscount(BigDecimal subtotalBeforeDiscount) { this.subtotalBeforeDiscount = subtotalBeforeDiscount; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getLoyaltyRank() { return loyaltyRank; }
    public void setLoyaltyRank(String loyaltyRank) { this.loyaltyRank = loyaltyRank; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public boolean isPagada() { return pagada; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }
}
