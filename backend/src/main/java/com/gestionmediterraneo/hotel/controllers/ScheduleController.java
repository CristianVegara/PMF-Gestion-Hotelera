package com.gestionmediterraneo.hotel.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public List<Schedule> getSchedules() {
        return scheduleService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.save(schedule);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public Schedule updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        return scheduleService.save(scheduleDetails); 
    }
}