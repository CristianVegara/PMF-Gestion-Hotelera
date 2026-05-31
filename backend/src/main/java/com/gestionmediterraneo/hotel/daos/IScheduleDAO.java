package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Schedule;


/**
 * Data Access Object for {@link Schedule} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IScheduleDAO extends CrudRepository<Schedule, Long>{

}
