package com.gestionmediterraneo.hotel.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.PaymentMethod;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnoreProperties({"payments", "hibernateLazyInitializer", "handler"})
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"payments", "invoices", "bookings", "activities"})
    private Client client;

    @NotNull(message = "Debe indicar el método de pago")
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @NotNull(message = "Debe indicar el importe")
    @Positive(message = "El importe debe ser mayor que 0")
    private BigDecimal amount;

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
