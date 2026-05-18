package com.gestionmediterraneo.hotel.entities;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.InvoiceItemType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnoreProperties({"items", "hibernateLazyInitializer", "handler"})
    private Invoice invoice;

    @NotNull(message = "Debe indicar el tipo de ítem")
    @Enumerated(EnumType.STRING)
    private InvoiceItemType type;

    @NotNull(message = "Debe indicar la descripción")
    @Size(min = 3, max = 255)
    private String description;

    @NotNull(message = "Debe indicar la cantidad")
    @Positive(message = "La cantidad debe ser mayor que 0")
    private Integer quantity;

    @NotNull(message = "Debe indicar el precio unitario")
    @Positive(message = "El precio unitario debe ser mayor que 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Debe indicar el total")
    @Positive(message = "El total debe ser mayor que 0")
    private BigDecimal amount;

    public InvoiceItem() {
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

    public InvoiceItemType getType() {
        return type;
    }

    public void setType(InvoiceItemType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
