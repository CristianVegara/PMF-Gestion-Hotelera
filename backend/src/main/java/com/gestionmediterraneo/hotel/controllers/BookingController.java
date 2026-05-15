package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:3000"})
public class BookingController {

    @Autowired
    private IBookingDAO bookingDao;

    @Autowired
    private IRoomDAO roomDao;

    
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }
    
    @GetMapping("/in-house")
    public ResponseEntity<?> getInHouseBookings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Booking> bookings = bookingDao.findBookingsByDate(date);
            
            if (bookings.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "No hay reservas para la fecha: " + date);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(bookings, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("-- ERROR EN BOOKINGS IN-HOUSE --");
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        Room room = roomDao.findById(booking.getHabitacion().getId()).orElse(null);
        if (room == null) {
            return ResponseEntity.badRequest().body("Error: La habitación no existe.");
        }

        boolean solapado = bookingDao.estaOcupada(
                booking.getHabitacion().getId(), 
                booking.getFechaEntrada(), 
                booking.getFechaSalida()
        );

        if (solapado) {
            return ResponseEntity.status(409).body("La habitación ya está reservada en esas fechas.");
        }

        Booking newBooking = bookingDao.save(booking);

        LocalDate hoy = LocalDate.now();
        if ((booking.getFechaEntrada().isBefore(hoy) || booking.getFechaEntrada().isEqual(hoy)) 
             && booking.getFechaSalida().isAfter(hoy)) {
            room.setStatus(RoomStatus.OCUPADA);
            roomDao.save(room);
        }

        return ResponseEntity.ok(newBooking);
    }
    
    @GetMapping("/room/{roomId}")
    public List<Booking> getBookingsByRoom(@PathVariable Long roomId) {
        return bookingDao.buscarPorHabitacion(roomId);
    }
    
    
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return bookingDao.findById(id).map(booking -> {
            booking.setFechaEntrada(bookingDetails.getFechaEntrada());
            booking.setFechaSalida(bookingDetails.getFechaSalida());
            if(bookingDetails.getCliente() != null) booking.setCliente(bookingDetails.getCliente());
            
            bookingDao.save(booking);
            
            Room room = booking.getHabitacion();
            LocalDate hoy = LocalDate.now();
            
           
            boolean deberiaEstarOcupada = (hoy.isEqual(booking.getFechaEntrada()) || hoy.isAfter(booking.getFechaEntrada())) 
                                          && hoy.isBefore(booking.getFechaSalida());

            if (deberiaEstarOcupada) {
                room.setStatus(RoomStatus.OCUPADA);
            } else {
                room.setStatus(RoomStatus.LIBRE);
            }
            roomDao.save(room);

            return ResponseEntity.ok(booking);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    	bookingDao.deleteById(id);
    }
}
