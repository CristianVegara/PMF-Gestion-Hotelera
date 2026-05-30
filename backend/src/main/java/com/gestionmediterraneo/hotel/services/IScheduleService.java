package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Schedule;

/**
 * Service interface for managing {@link Schedule} entities.
 *
 * @author Gestión Mediterráneo
 * @see Schedule
 */
public interface IScheduleService {
    /** Retrieve all schedules. */
    public List<Schedule> findAll();
    /** Save a schedule. */
    public Schedule save(Schedule schedule);
}
