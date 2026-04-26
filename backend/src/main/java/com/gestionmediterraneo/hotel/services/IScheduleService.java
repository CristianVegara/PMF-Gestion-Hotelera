package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Schedule;

public interface IScheduleService {
    public List<Schedule> findAll();
    public Schedule save(Schedule schedule);
}