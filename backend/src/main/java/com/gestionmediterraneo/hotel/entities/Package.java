package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Represents a hotel package that bundles rooms, activities, and discounts.
 *
 * @author Gestión Mediterráneo
 * @see Room
 * @see Activity
 * @see Discount
 */
@Entity
@Table(name = "packages")
public class Package {

	    /** Unique identifier for the package. */
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    /** Name of the package. */
	    private String nombre;
	    /** Price of the package. */
	    private Double precio;

	    /** Activities included in this package. */
	    @ManyToMany
	    @JoinTable(
	      name = "package_activities",
	      joinColumns = @JoinColumn(name = "paquete_id"),
	      inverseJoinColumns = @JoinColumn(name = "actividad_id"))
	    private List<Activity> actividades;

	    /** Rooms included in this package. */
	    @ManyToMany
	    @JoinTable(
	      name = "package_rooms",
	      joinColumns = @JoinColumn(name = "paquete_id"),
	      inverseJoinColumns = @JoinColumn(name = "habitacion_id"))
	    private List<Room> habitaciones;

	    /** Discounts included in this package. */
	    @ManyToMany
	    @JoinTable(
	      name = "package_discounts",
	      joinColumns = @JoinColumn(name = "paquete_id"),
	      inverseJoinColumns = @JoinColumn(name = "descuento_id"))
	    private List<Discount> descuentos;

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

		public Double getPrecio() {
			return precio;
		}

		public void setPrecio(Double precio) {
			this.precio = precio;
		}

		public List<Activity> getActividades() {
			return actividades;
		}

		public void setActividades(List<Activity> actividades) {
			this.actividades = actividades;
		}

		public List<Room> getHabitaciones() {
			return habitaciones;
		}

		public void setHabitaciones(List<Room> habitaciones) {
			this.habitaciones = habitaciones;
		}

		public List<Discount> getDescuentos() {
			return descuentos;
		}

		public void setDescuentos(List<Discount> descuentos) {
			this.descuentos = descuentos;
		}
}
