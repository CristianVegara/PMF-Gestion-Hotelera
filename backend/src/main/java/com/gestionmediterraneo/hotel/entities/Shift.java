package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a specific work shift assigned to an employee on a given date.
 *
 * <p>A shift links an {@link Employee} to a {@link Schedule} on a
 * particular {@code fecha} (date), with optional observations.</p>
 *
 * @author Gestión Mediterráneo
 * @see Employee
 * @see Schedule
 */
@Entity
@Table(name="shifts")
public class Shift {
    /** Unique identifier for the shift. */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** Date of the shift. */
    @Column(nullable = false)
    private LocalDate fecha;

    /** Employee assigned to this shift. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employee employee;

    /** Schedule (shift pattern) applied for this shift. */
    @JsonIgnoreProperties({"shifts"})
    @ManyToOne(fetch = FetchType.EAGER)
    private Schedule schedule;

    /** Optional observations or notes about this shift. */
    private String observaciones;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}



}
