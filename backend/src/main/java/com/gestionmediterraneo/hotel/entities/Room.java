package com.gestionmediterraneo.hotel.entities;

import com.gestionmediterraneo.hotel.enums.RoomStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents a hotel room with type, status, and pricing.
 *
 * <p>Rooms have a number, type (single, double, suite),
 * status (available, occupied, dirty, out of service),
 * a base price, and a dynamic price calculated at runtime.</p>
 *
 * @author Gestión Mediterráneo
 * @see RoomType
 * @see RoomStatus
 */
@Entity
@Table(name = "habitacion")
public class Room {

	/** Unique identifier for the room. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Room number, must be unique. */
    @NotBlank(message = "no puede estar vacío")
    @Column(name = "numero", nullable = false, unique = true)
    private String number;

    /** Type of the room (single, double, suite). */
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "tipo")
    private RoomType type;

    /** Current status of the room. */
    @NotNull(message = "no puede ser nulo")
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "estado")
    private RoomStatus status;

    /** Base price per night. */
    @NotNull(message = "no puede ser nulo")
    @Positive(message = "debe ser mayor que 0")
    @Column(name = "precio_por_noche")
    private Double price;

    /** Dynamically calculated price (not persisted). */
    @Transient
    private Double dynamicPrice;

    public Room() {}

    public Room(String number, RoomType type, RoomStatus status, Double price) {
        this.number = number;
        this.type = type;
        this.status = status;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

	public Double getDynamicPrice() {
		return dynamicPrice;
	}

	public void setDynamicPrice(Double dynamicPrice) {
		this.dynamicPrice = dynamicPrice;
	}


}
