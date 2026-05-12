package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client cliente;

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

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getCliente() { return cliente; }
    public void setCliente(Client cliente) { this.cliente = cliente; }

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
