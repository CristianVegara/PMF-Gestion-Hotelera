package com.gestionmediterraneo.hotel.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.gestionmediterraneo.hotel.entities.Schedule;
import com.gestionmediterraneo.hotel.services.IScheduleService;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private IScheduleService scheduleService;

    @GetMapping
    public List<Schedule> getSchedules() {
        return scheduleService.findAll();
    }

    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.save(schedule);
    }
    
    @PutMapping("/{id}")
    public Schedule updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        return scheduleService.save(scheduleDetails); 
    }
}