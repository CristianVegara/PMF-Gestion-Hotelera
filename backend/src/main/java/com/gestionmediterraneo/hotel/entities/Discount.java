package com.gestionmediterraneo.hotel.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a discount that can be applied to client bookings.
 *
 * <p>Discounts have a concept/description, a percentage value,
 * an expiration date, and a many-to-many relationship with clients.</p>
 *
 * @author Gestión Mediterráneo
 * @see Client
 */
@Entity
@Table(name = "discounts")
public class Discount implements Serializable {

    /** Unique identifier for the discount. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Description/concept of the discount. */
    @NotEmpty(message = "no puede estar vacío")
    private String concepto;

    /** Discount percentage value (e.g., 10 for 10%). */
    @NotNull(message = "no puede estar vacío")
    private Double porcentaje;

    /** Expiration date of the discount. */
    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    /** List of clients eligible for this discount. */
    @ManyToMany
    @JoinTable(
        name = "client_discount",
        joinColumns = @JoinColumn(name = "discount_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    @JsonIgnore
    private List<Client> clients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    private static final long serialVersionUID = 1L;
}
