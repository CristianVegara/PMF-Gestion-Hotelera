package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * Represents a system user with authentication credentials and role.
 *
 * <p>A user has a username, password hash, role, creation date,
 * and an optional linked {@link Employee} record.</p>
 *
 * @author Gestión Mediterráneo
 * @see UserRole
 * @see Employee
 */
@Entity
@Table(name="users")
public class User {

	/** Unique identifier for the user. */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	/** Username, must be unique. */
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false, unique=true)
	private String username;

	/** Hashed password for authentication. */
	@NotEmpty(message="No puede estar vacío")
	@Column(name="password_hash", nullable=false)
	private String passwordHash;

	/** User's role in the system. */
	private String role;

	/** Linked employee record (nullable). */
	@OneToOne(mappedBy = "user")
	@JsonIgnoreProperties({"user"})
	private Employee employee;

	/** Date when the user account was created. */
	@Column(name="created_at")
	private LocalDate createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
