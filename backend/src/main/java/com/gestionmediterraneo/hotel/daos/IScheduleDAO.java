package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Schedule;


public interface IScheduleDAO extends CrudRepository<Schedule, Long>{

}
