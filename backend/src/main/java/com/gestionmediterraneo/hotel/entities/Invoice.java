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
    private BigDecimal subtotal;
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

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public boolean isPagada() { return pagada; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }
}