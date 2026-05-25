package com.gestionmediterraneo.hotel.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getAllShifts() {
        List<Shift> shifts = null;
        Map<String, Object> response = new HashMap<>();

        try {
            shifts = shiftService.findAllWithDetails();
        } catch (Exception e) {
            System.err.println("-- ERROR EN SHIFTS --");
            e.printStackTrace();
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Shift>>(shifts, HttpStatus.OK);
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getShiftsByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Shift> shifts = null;
        Map<String, Object> response = new HashMap<>();

        try {
            shifts = shiftService.findShiftsByRange(start, end);
        } catch (Exception e) {
            System.err.println("-- ERROR EN SHIFTS POR RANGO --");
            e.printStackTrace();
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Shift>>(shifts, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public ResponseEntity<?> createShift(@RequestBody Shift shift) {
        Shift newShift = null;
        Map<String, Object> response = new HashMap<>();

        try {
            newShift = shiftService.save(shift);
        } catch (Exception e) {
            System.err.println("-- ERROR AL CREAR SHIFT --");
            e.printStackTrace();
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Shift>(newShift, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR')")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            shiftService.delete(id);
        } catch (Exception e) {
            System.err.println("-- ERROR AL ELIMINAR SHIFT --");
            e.printStackTrace();
            response.put("mensaje", "Error al eliminar el turno en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El turno ha sido eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public ResponseEntity<?> updateShift(@PathVariable Long id, @RequestBody Shift shiftDetails) {
        Shift shift = shiftService.findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (shift == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        shift.setFecha(shiftDetails.getFecha());
        shift.setEmployee(shiftDetails.getEmployee());
        shift.setSchedule(shiftDetails.getSchedule());
        shift.setObservaciones(shiftDetails.getObservaciones());
        
        return ResponseEntity.ok(shiftService.save(shift));
    }
}