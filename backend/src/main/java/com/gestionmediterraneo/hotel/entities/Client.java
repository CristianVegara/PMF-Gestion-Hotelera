package com.gestionmediterraneo.hotel.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

/**
 * Represents a hotel client with personal information and related data.
 *
 * <p>A client can have multiple bookings, invoices, payments, refunds,
 * and can participate in activities. Also linked to discounts.</p>
 *
 * @author Gestión Mediterráneo
 * @see Booking
 * @see Invoice
 * @see Activity
 */
@Entity
@Table(name="clients")
public class Client {

	/** Unique identifier for the client. */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;

	/** Client's DNI (national ID number), must be unique. */
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=true)
	String dni;

	/** Client's full name. */
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=false)
	String nombre;

	/** Client's phone number. */
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=false)
	String telefono;

	/** Client's email address, must be unique. */
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=true)
	String correo;

	@ManyToMany(mappedBy = "clients")
	@JsonIgnoreProperties("clients")
	private List<Activity> activities;

	@ManyToMany(mappedBy = "clients")
	@JsonIgnore
	private List<Discount> discounts;

	@JsonIgnoreProperties({"cliente", "hibernateLazyInitializer", "handler"})
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Invoice> invoices = new ArrayList<>();

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("cliente")
	private List<Booking> bookings = new ArrayList<>();

	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties({"client", "invoice"})
	private List<Payment> payments = new ArrayList<>();

	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties({"client", "payment"})
	private List<Refund> refunds = new ArrayList<>();

	public List<Payment> getPayments() {
	    return payments;
	}

	public void setPayments(List<Payment> payments) {
	    this.payments = payments;
	}

	public List<Refund> getRefunds() {
	    return refunds;
	}

	public void setRefunds(List<Refund> refunds) {
	    this.refunds = refunds;
	}


	public Long getId() {
		return id;
	}

	public String getDni() {
		return dni;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getCorreo() {
		return correo;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public List<Activity> getActivities() {
	    return activities;
	}

	public void setActivities(List<Activity> activities) {
	    this.activities = activities;
	}

	public List<Discount> getDiscounts() {
	    return discounts;
	}

	public void setDiscounts(List<Discount> discounts) {
	    this.discounts = discounts;
	}


	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

}
