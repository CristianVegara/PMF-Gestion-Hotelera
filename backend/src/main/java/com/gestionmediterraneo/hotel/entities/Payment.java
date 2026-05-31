package com.gestionmediterraneo.hotel.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.PaymentMethod;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a payment made by a client against an invoice.
 *
 * <p>A payment records the payment method, amount, and date.
 * It is linked to both an {@link Invoice} and a {@link Client}.</p>
 *
 * @author Gestión Mediterráneo
 * @see PaymentMethod
 * @see Invoice
 */
@Entity
@Table(name = "payments")
public class Payment {

    /** Unique identifier for the payment. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Invoice this payment is for. */
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnoreProperties({"payments", "hibernateLazyInitializer", "handler"})
    private Invoice invoice;

    /** Client who made the payment. */
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"payments", "invoices", "bookings", "activities"})
    private Client client;

    /** Payment method used (cash, card, transfer, etc.). */
    @NotNull(message = "Debe indicar el método de pago")
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    /** Payment amount (must be positive). */
    @NotNull(message = "Debe indicar el importe")
    @Positive(message = "El importe debe ser mayor que 0")
    private BigDecimal amount;

    /** Date when the payment was made. */
    @NotNull(message = "Debe indicar la fecha de pago")
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
