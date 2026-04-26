package com.gestionmediterraneo.hotel.controllers;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public List<Shift> getAllShifts() {
        return shiftService.findAll();
    }

    @GetMapping("/range")
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
    public Shift createShift(@RequestBody Shift shift) {
        return shiftService.save(shift);
    }

    @DeleteMapping("/{id}")
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