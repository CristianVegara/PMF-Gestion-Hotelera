package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private IBookingDAO bookingRepository;

    @Autowired
    private IRoomDAO roomRepository;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        Room room = roomRepository.findById(booking.getHabitacion().getId()).orElse(null);
        if (room == null) {
            return ResponseEntity.badRequest().body("Error: La habitación no existe.");
        }

        boolean solapado = bookingRepository.estaOcupada(
                booking.getHabitacion().getId(), 
                booking.getFechaEntrada(), 
                booking.getFechaSalida()
        );

        if (solapado) {
            return ResponseEntity.status(409).body("La habitación ya está reservada en esas fechas.");
        }

        Booking newBooking = bookingRepository.save(booking);

        LocalDate hoy = LocalDate.now();
        if ((booking.getFechaEntrada().isBefore(hoy) || booking.getFechaEntrada().isEqual(hoy)) 
             && booking.getFechaSalida().isAfter(hoy)) {
            room.setStatus("Ocupada");
            roomRepository.save(room);
        }

        return ResponseEntity.ok(newBooking);
    }
    
    @GetMapping("/room/{roomId}")
    public List<Booking> getBookingsByRoom(@PathVariable Long roomId) {
        return bookingRepository.buscarPorHabitacion(roomId);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return bookingRepository.findById(id).map(booking -> {
            booking.setFechaEntrada(bookingDetails.getFechaEntrada());
            booking.setFechaSalida(bookingDetails.getFechaSalida());
            if(bookingDetails.getCliente() != null) booking.setCliente(bookingDetails.getCliente());
            
            bookingRepository.save(booking);
            
            Room room = booking.getHabitacion();
            LocalDate hoy = LocalDate.now();
            
           
            boolean deberiaEstarOcupada = (hoy.isEqual(booking.getFechaEntrada()) || hoy.isAfter(booking.getFechaEntrada())) 
                                          && hoy.isBefore(booking.getFechaSalida());

            if (deberiaEstarOcupada) {
                room.setStatus("Ocupada");
            } else {
                room.setStatus("Disponible");
            }
            roomRepository.save(room);

            return ResponseEntity.ok(booking);
        }).orElse(ResponseEntity.notFound().build());
    }
}
