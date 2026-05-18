package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionmediterraneo.hotel.daos.IChargeDAO;
import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.entities.Charge;
import com.gestionmediterraneo.hotel.entities.Booking;

@RestController
@RequestMapping("/api/charges")
@CrossOrigin(origins = {"http://localhost:3000"})
public class ChargeController {

    @Autowired
    private IChargeDAO chargeDao;

    @Autowired
    private IBookingDAO bookingDao;

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getChargesByBooking(@PathVariable Long bookingId) {
        try {
            List<Charge> charges = chargeDao.findByBookingId(bookingId);
            return ResponseEntity.ok(charges);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Error al obtener cargos");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<?> createCharge(@PathVariable Long bookingId, @RequestBody Charge charge) {
        Map<String, Object> response = new HashMap<>();
        try {
            Booking booking = bookingDao.findById(bookingId).orElse(null);
            if (booking == null) {
                response.put("mensaje", "Reserva no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            charge.setBooking(booking);
            Charge saved = chargeDao.save(charge);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar cargo");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCharge(@PathVariable Long id, @RequestBody Charge chargeData) {
        Map<String, Object> response = new HashMap<>();
        return chargeDao.findById(id)
                .map(charge -> {
                    charge.setDescription(chargeData.getDescription());
                    charge.setAmount(chargeData.getAmount());
                    charge.setType(chargeData.getType());
                    charge.setDate(chargeData.getDate());
                    charge.setAppliedToInvoice(chargeData.isAppliedToInvoice());
                    chargeDao.save(charge);
                    return ResponseEntity.ok().body((Object) charge);
                })
                .orElseGet(() -> {
                    response.put("mensaje", "Cargo no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharge(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!chargeDao.existsById(id)) {
                response.put("mensaje", "Cargo no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            chargeDao.deleteById(id);
            response.put("mensaje", "Cargo eliminado");
            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar cargo");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
