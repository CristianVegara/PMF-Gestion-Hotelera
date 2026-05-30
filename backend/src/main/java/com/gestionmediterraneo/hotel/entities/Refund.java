package com.gestionmediterraneo.hotel.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Represents a refund issued to a client.
 *
 * <p>A refund is linked to a {@link Payment}, optionally a {@link Booking},
 * and a {@link Client}. It records the amount, date, and reason for the refund.</p>
 *
 * @author Gestión Mediterráneo
 * @see Payment
 * @see Client
 */
@Entity
@Table(name = "refunds")
public class Refund {

    /** Unique identifier for the refund. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Payment associated with this refund. */
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonIgnoreProperties({"refunds", "hibernateLazyInitializer", "handler"})
    private Payment payment;

    /** Booking associated with this refund (nullable). */
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    /** Client receiving the refund. */
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"payments", "invoices", "bookings", "activities"})
    private Client client;

    /** Refund amount (must be positive). */
    @NotNull(message = "Debe indicar el importe del reembolso")
    @Positive(message = "El importe debe ser mayor que 0")
    private BigDecimal amount;

    /** Date when the refund was issued. */
    @NotNull(message = "Debe indicar la fecha del reembolso")
    @Column(name = "refund_date")
    private LocalDate refundDate;

    /** Reason for the refund. */
    @Size(max = 255)
    private String reason;

    public Refund() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDate refundDate) {
        this.refundDate = refundDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
