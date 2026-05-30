package com.gestionmediterraneo.hotel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Represents a hotel employee linked to a {@link User} account.
 *
 * @author Gestión Mediterráneo
 * @see User
 */
@Entity
@Table(name="employees")
public class Employee {
    /** Unique identifier for the employee. */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** Employee's first name. */
    private String nombre;
    /** Employee's last name. */
    private String apellido;
    /** Employee's job title/role. */
    private String cargo;

    /** Linked user account for this employee. */
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})    @JoinColumn(name = "user_id")
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
