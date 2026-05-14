package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="booking")
public class Booking {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String estado;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties("bookings")
    private Client cliente;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room habitacion;
    
    public enum BookingStatus {
	    SIN_CONFIRMAR, CONFIRMADA, TERMINADA, CANCELADA
	}
    
    @PrePersist
    @PreUpdate 
    public void asignarEstadoAutomatico() {
        LocalDate hoy = LocalDate.now();

        if (this.fechaEntrada == null || this.fechaSalida == null) {
            this.estado = "PENDIENTE";
            return;
        }

        if (hoy.isAfter(this.fechaSalida)) {
            this.estado = "TERMINADA";
        } else if (!hoy.isBefore(this.fechaEntrada)) {
            this.estado = "CONFIRMADA";
        } else {
            this.estado = "PRÓXIMA";
        }
    }
	public Booking() {
	}
	
	public Booking(Long id, LocalDate fechaEntrada, LocalDate fechaSalida, String estado, Client cliente,
			Room habitacion) {
		this.id = id;
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		this.cliente = cliente;
		this.habitacion = habitacion;
		asignarEstadoAutomatico();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(LocalDate fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public LocalDate getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(LocalDate fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public String getEstado() {
		return estado;
	}


	public Client getCliente() {
		return cliente;
	}

	public void setCliente(Client cliente) {
		this.cliente = cliente;
	}

	public Room getHabitacion() {
		return habitacion;
	}

	public void setHabitacion(Room habitacion) {
		this.habitacion = habitacion;
	}

	
}