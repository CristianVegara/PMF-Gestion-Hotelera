package com.gestionmediterraneo.hotel.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="activities")
public class Activity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	
	@NotEmpty(message="No puede estar vacío")
	@Column(nullable=false)
	String descripcion;
	
	@NotNull(message="No puede estar vacío")
	@Column(nullable=false)
	Double precio;	

    @NotNull(message="No puede estar vacío")
    @Column(name="fecha_comienzo", columnDefinition = "DATETIME")
    private LocalDateTime fechaComienzo;

    @NotNull(message="No puede estar vacío")
    @Column(name="fecha_fin", columnDefinition = "DATETIME")
    private LocalDateTime fechaFin;
    
    @NotNull(message="No puede estar vacío")
    @Column(nullable=false)
    private byte maxParticipantes;
    
    @ManyToMany
    @JoinTable(
        name = "activities_clients",
        joinColumns = @JoinColumn(name = "activity_id"),
        inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    @JsonIgnoreProperties({"activities", "bookings"}) 
    private List<Client> clients;
  


	public Long getId() {
		return id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Double getPrecio() {
		return precio;
	}

	public LocalDateTime getFechaComienzo() {
		return fechaComienzo;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public void setFechaComienzo(LocalDateTime fechaComienzo) {
		this.fechaComienzo = fechaComienzo;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	public List<Client> getClients() {
	    return clients;
	}

	public void setClients(List<Client> clients) {
	    this.clients = clients;
	}

	public byte getMaxParticipantes() {
		return maxParticipantes;
	}

	public void setMaxParticipantes(byte maxParticipantes) {
		this.maxParticipantes = maxParticipantes;
	}
	
	
	
}