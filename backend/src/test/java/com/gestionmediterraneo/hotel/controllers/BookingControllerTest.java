package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.BookingStatus;
import com.gestionmediterraneo.hotel.enums.CheckInStatus;
import com.gestionmediterraneo.hotel.security.JwtUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BookingController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingDAO bookingDao;

    @MockBean
    private IRoomDAO roomDao;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllBookings() throws Exception {
        when(bookingDao.findAll()).thenReturn(Arrays.asList(new Booking(), new Booking()));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBookingByIdWhenExists() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        when(bookingDao.findById(1L)).thenReturn(Optional.of(booking));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenBookingDoesNotExistById() throws Exception {
        when(bookingDao.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetByIdFails() throws Exception {
        when(bookingDao.findById(1L)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnInHouseBookingsWhenExist() throws Exception {
        when(bookingDao.findByCheckInStatus(CheckInStatus.DENTRO)).thenReturn(Arrays.asList(new Booking()));

        mockMvc.perform(get("/api/bookings/in-house/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenNoInHouseBookings() throws Exception {
        when(bookingDao.findByCheckInStatus(CheckInStatus.DENTRO)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/bookings/in-house/"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetInHouseFails() throws Exception {
        when(bookingDao.findByCheckInStatus(CheckInStatus.DENTRO)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/bookings/in-house/"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBookingsByDateWhenExist() throws Exception {
        LocalDate date = LocalDate.now();
        when(bookingDao.findBookingsByDate(eq(date), eq(BookingStatus.CANCELADA))).thenReturn(Arrays.asList(new Booking()));

        mockMvc.perform(get("/api/bookings/date")
                .param("date", date.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenNoBookingsByDate() throws Exception {
        LocalDate date = LocalDate.now();
        when(bookingDao.findBookingsByDate(eq(date), eq(BookingStatus.CANCELADA))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bookings/date")
                .param("date", date.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetByDateFails() throws Exception {
        LocalDate date = LocalDate.now();
        when(bookingDao.findBookingsByDate(eq(date), eq(BookingStatus.CANCELADA))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/bookings/date")
                .param("date", date.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCreateBookingSuccessfully() throws Exception {
        Booking booking = new Booking();
        when(bookingDao.save(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnInternalServerErrorWhenCreationFails() throws Exception {
        Booking booking = new Booking();
        when(bookingDao.save(any(Booking.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBookingsByRoom() throws Exception {
        when(bookingDao.buscarPorHabitacion(1L)).thenReturn(Arrays.asList(new Booking()));

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
        existingBooking.setFechaEntrada(LocalDate.now().minusDays(1));
        existingBooking.setFechaSalida(LocalDate.now().plusDays(2));

        Booking details = new Booking();
        details.setFechaEntrada(LocalDate.now().minusDays(1));
        details.setFechaSalida(LocalDate.now().plusDays(2));
        details.setHabitacion(room);

        when(bookingDao.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingDao.save(any(Booking.class))).thenReturn(existingBooking);
        when(roomDao.save(any(Room.class))).thenReturn(room);

        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingBooking() throws Exception {
        when(bookingDao.findById(anyLong())).thenReturn(Optional.empty());

        Booking details = new Booking();

        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteBookingSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isOk());

        verify(bookingDao).deleteById(1L);
    }
}