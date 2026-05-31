package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * Represents a work schedule (shift pattern) for hotel employees.
 *
 * <p>A schedule defines a shift name, start time, and end time.
 * Employees are assigned to schedules via {@link Shift} entities.</p>
 *
 * @author Gestión Mediterráneo
 * @see Shift
 * @see Employee
 */
@Entity
@Table(name="schedules")
public class Schedule {
    /** Unique identifier for the schedule. */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** Name of the shift (e.g., "Morning", "Afternoon"). */
    private String nombreTurno;

    /** Start time of the shift. */
    @Column(name="hora_entrada")
    private LocalTime horaEntrada;

    /** End time of the shift. */
    @Column(name="hora_salida")
    private LocalTime horaSalida;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreTurno() {
		return nombreTurno;
	}

	public void setNombreTurno(String nombreTurno) {
		this.nombreTurno = nombreTurno;
	}

	public LocalTime getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(LocalTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public LocalTime getHoraSalida() {
		return horaSalida;
	}

	public void setHoraSalida(LocalTime horaSalida) {
		this.horaSalida = horaSalida;
	}


}
