package com.gestionmediterraneo.hotel.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gestionmediterraneo.hotel.entities.Schedule;
import com.gestionmediterraneo.hotel.services.IScheduleService;

/**
 * REST controller for managing {@link Schedule} resources.
 *
 * @author Gestión Mediterráneo
 */
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private IScheduleService scheduleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public List<Schedule> getSchedules() {
        return scheduleService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.save(schedule);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public Schedule updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        return scheduleService.save(scheduleDetails);
    }
}
