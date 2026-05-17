package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.BookingStatus;
import com.gestionmediterraneo.hotel.enums.CheckInStatus;
import com.gestionmediterraneo.hotel.enums.RoomStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){    	
    	try {
        	Booking booking = bookingDao.findById(id).orElse(null);
        	
        	if(booking == null) {
        		   Map<String, Object> response = new HashMap<>();
                   response.put("mensaje", "No hay reservas con ese id");
                   return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        	}
        	
        	
        	return new ResponseEntity<>(booking, HttpStatus.OK);
        	
    	} catch (Exception e) {
    		Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    	}    	
    
    }
    
    
    @GetMapping("/in-house/")
    public ResponseEntity<?> getInHouseBookings() {
        try {
            List<Booking> bookings = bookingDao.findByCheckInStatus(CheckInStatus.DENTRO);
            
            if (bookings.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "No hay clientes in-house");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(bookings, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/date")
    public ResponseEntity<?> getBookingsByDate(
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
        try {
            booking.setEstado(BookingStatus.CONFIRMADA);
            booking.setCheckInStatus(CheckInStatus.PENDIENTE);
            
            Booking savedBooking = bookingDao.save(booking);
            return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("-- ERROR AL CREAR RESERVA --");
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Error al realizar la inserción en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            
            if (bookingDetails.getCliente() != null) {
                booking.setCliente(bookingDetails.getCliente());
            }
            if (bookingDetails.getEstado() != null) {
                booking.setEstado(bookingDetails.getEstado());
            }
            if (bookingDetails.getCheckInStatus() != null) {
                booking.setCheckInStatus(bookingDetails.getCheckInStatus());
            }
            if (bookingDetails.getRoomType() != null) {
                booking.setRoomType(bookingDetails.getRoomType());
            }
            if (bookingDetails.getHabitacion() != null) {
                booking.setHabitacion(bookingDetails.getHabitacion());
            }
            
            bookingDao.save(booking);
            
            Room room = booking.getHabitacion();
            if (room != null) {
                LocalDate hoy = LocalDate.now();
                boolean deberiaEstarOcupada = (hoy.isEqual(booking.getFechaEntrada()) || hoy.isAfter(booking.getFechaEntrada())) 
                                              && hoy.isBefore(booking.getFechaSalida());

                if (deberiaEstarOcupada) {
                    room.setStatus(RoomStatus.OCUPADA);
                } else {
                    room.setStatus(RoomStatus.LIBRE);
                }
                roomDao.save(room);
            }

            return ResponseEntity.ok(booking);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    	bookingDao.deleteById(id);
    }
}
