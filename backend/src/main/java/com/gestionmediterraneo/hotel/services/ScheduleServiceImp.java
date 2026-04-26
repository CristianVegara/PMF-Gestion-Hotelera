package com.gestionmediterraneo.hotel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IScheduleDAO;
import com.gestionmediterraneo.hotel.entities.Schedule;

@Service
public class ScheduleServiceImp implements IScheduleService {

    @Autowired
    private IScheduleDAO scheduleDao;

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findAll() {
        return (List<Schedule>) scheduleDao.findAll();
    }

    @Override
    @Transactional
    public Schedule save(Schedule schedule) {
        return scheduleDao.save(schedule);
    }
}