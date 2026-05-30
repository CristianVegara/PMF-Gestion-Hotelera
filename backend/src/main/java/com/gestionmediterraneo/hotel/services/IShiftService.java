package com.gestionmediterraneo.hotel.services;

import java.time.LocalDate;
import java.util.List;

import com.gestionmediterraneo.hotel.entities.Shift;

/**
 * Service interface for managing {@link Shift} entities.
 *
 * @author Gestión Mediterráneo
 * @see Shift
 */
public interface IShiftService {
    /** Retrieve all shifts. */
    public List<Shift> findAll();
    /** Find shifts within a date range. */
    public List<Shift> findShiftsByRange(LocalDate start, LocalDate end);
    /** Save a shift. */
    public Shift save(Shift shift);
    /** Delete a shift by ID. */
    public void delete(Long id);
    /** Find all shifts with employee and schedule eagerly fetched. */
    List<Shift> findAllWithDetails();
}
