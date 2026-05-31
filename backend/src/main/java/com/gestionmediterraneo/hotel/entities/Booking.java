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

/**
 * Represents a hotel room booking made by a client.
 *
 * <p>A booking includes check-in/check-out dates, room type,
 * booking status, check-in status, and the associated client and room.</p>
 *
 * @author Gestión Mediterráneo
 * @see Client
 * @see Room
 * @see BookingStatus
 */
@Entity
@Table(name="booking")
public class Booking {

    /** Unique identifier for the booking. */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** Check-in date for this booking. */
    @NotNull(message = "no puede ser nulo")
    private LocalDate fechaEntrada;

    /** Check-out date for this booking. */
    @NotNull(message = "no puede ser nulo")
    private LocalDate fechaSalida;

    /** Current status of the booking (confirmed, cancelled, etc.). */
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL)
    private BookingStatus estado;

    /** Current check-in status (pending, checked in, checked out). */
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL)
    private CheckInStatus checkInStatus;

    /** Required room type for the booking (used when no specific room is assigned). */
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "room_type")
    private RoomType roomType;

    /** Client who made this booking. */
    @NotNull(message = "no puede ser nulo")
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"bookings", "activities", "invoices", "payments", "refunds"})
    private Client cliente;

    /** Specific room assigned to this booking (nullable). */
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room habitacion;

    /** List of charges associated with this booking. */
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
