package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionmediterraneo.hotel.enums.BookingStatus;
import com.gestionmediterraneo.hotel.enums.CheckInStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

@Entity
@Table(name="booking")
public class Booking {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "no puede ser nulo")
    private LocalDate fechaEntrada;
    
    @NotNull(message = "no puede ser nulo")
    private LocalDate fechaSalida;
    
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL) 
    private BookingStatus estado;
    

    
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL) 
    private CheckInStatus checkInStatus;

    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL) 
    @Column(name = "room_type")
    private RoomType roomType;
    
    @NotNull(message = "no puede ser nulo")
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"bookings", "activities"})
    private Client cliente;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room habitacion;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"booking"})
    private List<Charge> charges = new ArrayList<>();       
 
	

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

	public BookingStatus getEstado() {
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
	public CheckInStatus getCheckInStatus() {
		return checkInStatus;
	}
	public void setCheckInStatus(CheckInStatus checkInStatus) {
		this.checkInStatus = checkInStatus;
	}
	public void setEstado(BookingStatus estado) {
		this.estado = estado;
	}
	public RoomType getRoomType() {
		return roomType;
	}
	
	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}	
	
	
}