package com.gestionmediterraneo.hotel.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.ChargeType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "charges")
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnoreProperties({"charges", "hibernateLazyInitializer", "handler"})
    private Booking booking;

    @NotNull(message = "Debe indicar el tipo de cargo")
    @Enumerated(EnumType.STRING)
    private ChargeType type;

    @NotNull(message = "Debe indicar una descripción")
    @Size(min = 3, max = 255)
    private String description;

    @NotNull(message = "Debe indicar un importe")
    @Positive(message = "El importe debe ser mayor que 0")
    private BigDecimal amount;

    @NotNull(message = "Debe indicar la fecha del cargo")
    @Column(name = "charge_date")
    private LocalDate date;

    @Column(name = "applied_to_invoice")
    private boolean appliedToInvoice = false;

    public Charge() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public ChargeType getType() {
        return type;
    }

    public void setType(ChargeType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isAppliedToInvoice() {
        return appliedToInvoice;
    }

    public void setAppliedToInvoice(boolean appliedToInvoice) {
        this.appliedToInvoice = appliedToInvoice;
    }
}
