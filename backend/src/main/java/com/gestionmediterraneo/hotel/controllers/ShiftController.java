package com.gestionmediterraneo.hotel.controllers;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gestionmediterraneo.hotel.entities.Shift;
import com.gestionmediterraneo.hotel.services.IShiftService;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private IShiftService shiftService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public List<Shift> getAllShifts() {
        return shiftService.findAll();
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public List<Shift> getShiftsByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            return shiftService.findShiftsByRange(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public Shift createShift(@RequestBody Shift shift) {
        return shiftService.save(shift);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public void deleteShift(@PathVariable Long id) {
        shiftService.delete(id);
    }
    
    @PutMapping("/{id}")
    public Shift updateShift(@PathVariable Long id, @RequestBody Shift shiftDetails) {
        try {
            Shift shift = shiftService.findAll().stream()
                            .filter(s -> s.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Turno no encontrado con id: " + id));
            
            shift.setFecha(shiftDetails.getFecha());
            shift.setEmployee(shiftDetails.getEmployee());
            shift.setSchedule(shiftDetails.getSchedule());
            shift.setObservaciones(shiftDetails.getObservaciones());
            
            return shiftService.save(shift);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}