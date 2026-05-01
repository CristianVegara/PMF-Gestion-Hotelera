package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.entities.Client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingDAO bookingRepository;

    @MockBean
    private IRoomDAO roomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllBookings() throws Exception {
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(new Booking(), new Booking()));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateBookingWhenRoomIsAvailable() throws Exception {
        Room room = new Room();
        room.setId(1L);
        
        Booking booking = new Booking();
        booking.setHabitacion(room);
        booking.setFechaEntrada(LocalDate.now().plusDays(1));
        booking.setFechaSalida(LocalDate.now().plusDays(3));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.estaOcupada(anyLong(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnConflictWhenRoomIsAlreadyOccupied() throws Exception {
        Room room = new Room();
        room.setId(1L);
        
        Booking booking = new Booking();
        booking.setHabitacion(room);
        booking.setFechaEntrada(LocalDate.now());
        booking.setFechaSalida(LocalDate.now().plusDays(2));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.estaOcupada(anyLong(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isConflict()); 
    }

    @Test
    void shouldReturnBadRequestWhenRoomDoesNotExist() throws Exception {
        Booking booking = new Booking();
        Room room = new Room();
        room.setId(99L);
        booking.setHabitacion(room);

        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBookingsByRoom() throws Exception {
        when(bookingRepository.buscarPorHabitacion(1L)).thenReturn(Arrays.asList(new Booking()));

        mockMvc.perform(get("/api/bookings/room/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateBookingWhenExists() throws Exception {
        Long bookingId = 1L;
        Room room = new Room();
        room.setId(1L);

        Booking existingBooking = new Booking();
        existingBooking.setId(bookingId);
        existingBooking.setHabitacion(room);

        Booking details = new Booking();
        details.setFechaEntrada(LocalDate.now().plusDays(5));
        details.setFechaSalida(LocalDate.now().plusDays(10));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(existingBooking);

        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingBooking() throws Exception {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        Booking details = new Booking();
        details.setFechaEntrada(LocalDate.now());

        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isNotFound());
    }
}