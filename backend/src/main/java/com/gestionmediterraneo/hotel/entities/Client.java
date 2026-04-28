package com.gestionmediterraneo.hotel.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name="clients")
public class Client {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=true)
	String dni;
	
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=false)
	String nombre;
	
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=false)
	String telefono;
	
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=true)
	String correo;
	
	@ManyToMany(mappedBy = "clients")
    @JsonIgnore
	private List<Activity> activities;
	
	@ManyToMany(mappedBy = "clients")
	@JsonIgnore
	private List<Discount> discounts;
	

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

	
}
