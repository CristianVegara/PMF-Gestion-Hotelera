package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
	
	
	
}
