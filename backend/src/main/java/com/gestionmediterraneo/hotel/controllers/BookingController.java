package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.BookingStatus;
import com.gestionmediterraneo.hotel.enums.CheckInStatus;
import com.gestionmediterraneo.hotel.enums.RoomStatus;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.services.AuditLogService;
import com.gestionmediterraneo.hotel.services.BillingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private BillingService billingService;

    @Autowired
    private AuditLogService auditLogService;


    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getAllBookings() {
        List<Booking> bookings = null;
        Map<String, Object> response = new HashMap<>();

        try {
            bookings = bookingDao.findAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Booking>>(bookings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
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
    @PreAuthorize("hasAnyRole('USER')")
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
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getBookingsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Booking> bookings = bookingDao.findBookingsByDate(date, BookingStatus.CANCELADA);

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
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            Booking savedBooking = bookingDao.save(booking);
            Invoice pendingInvoice = billingService.createPendingInvoiceForBooking(savedBooking);
            auditLogService.record(
                    "RESERVA_CREADA",
                    "Booking",
                    savedBooking.getId(),
                    "Reserva creada para cliente ID " + (savedBooking.getCliente() != null ? savedBooking.getCliente().getId() : "-")
                            + " del " + savedBooking.getFechaEntrada()
                            + " al " + savedBooking.getFechaSalida());
            auditLogService.record(
                    "FACTURA_PENDIENTE_GENERADA",
                    "Invoice",
                    pendingInvoice != null ? pendingInvoice.getId() : null,
                    "Factura pendiente creada automáticamente para reserva ID " + savedBooking.getId());
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
    @PreAuthorize("hasAnyRole('USER')")
    public List<Booking> getBookingsByRoom(@PathVariable Long roomId) {
        return bookingDao.buscarPorHabitacion(roomId);
    }


    @PutMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {

        return bookingDao.findById(id).map(booking -> {

            booking.setFechaEntrada(bookingDetails.getFechaEntrada());
            booking.setFechaSalida(bookingDetails.getFechaSalida());

            booking.setCliente(bookingDetails.getCliente());

            booking.setEstado(bookingDetails.getEstado());
            bookingDao.saveAndFlush(booking);

            booking.setEstado(bookingDetails.getEstado());

            booking.setCheckInStatus(bookingDetails.getCheckInStatus());
            booking.setRoomType(bookingDetails.getRoomType());

            Room room = null;

            if (bookingDetails.getHabitacion() != null) {
                booking.setHabitacion(bookingDetails.getHabitacion());
                room = booking.getHabitacion();
            }

            bookingDao.saveAndFlush(booking);

            if (room != null) {
                roomDao.save(room);
            }

            return ResponseEntity.ok(booking);

        }).orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR')")
    public void delete(@PathVariable Long id) {
    	bookingDao.deleteById(id);
    }
}
