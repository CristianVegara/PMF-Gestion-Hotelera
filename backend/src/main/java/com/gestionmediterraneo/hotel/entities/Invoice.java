package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.InvoiceStatus;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client cliente;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonIgnoreProperties({"charges", "cliente"})
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room habitacion;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"invoice"})
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"invoice"})
    private List<Payment> payments = new ArrayList<>();

    private String concepto;
    private int noches;
    private BigDecimal precio;
    private BigDecimal subtotalBeforeDiscount;
    private BigDecimal subtotal;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private String loyaltyRank;
    private BigDecimal iva;
    private BigDecimal total;
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
