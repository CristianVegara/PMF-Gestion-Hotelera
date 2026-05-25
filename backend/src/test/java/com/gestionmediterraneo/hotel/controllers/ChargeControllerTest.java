package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IChargeDAO;
import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.entities.Charge;
import com.gestionmediterraneo.hotel.enums.ChargeType;
import com.gestionmediterraneo.hotel.security.JwtUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChargeController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class ChargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IChargeDAO chargeDao;

    @MockBean
    private IBookingDAO bookingDao;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnChargesByBookingSuccessfully() throws Exception {
        when(chargeDao.findByBookingId(1L)).thenReturn(Arrays.asList(new Charge(), new Charge()));

        mockMvc.perform(get("/api/charges/booking/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetChargesByBookingThrowsDataAccessException() throws Exception {
        when(chargeDao.findByBookingId(anyLong())).thenThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")));

        mockMvc.perform(get("/api/charges/booking/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCreateChargeSuccessfully() throws Exception {
        Booking booking = new Booking();
        Charge charge = new Charge();
        charge.setDescription("Mini-bar entry");

        when(bookingDao.findById(1L)).thenReturn(Optional.of(booking));
        when(chargeDao.save(any(Charge.class))).thenReturn(charge);

        mockMvc.perform(post("/api/charges/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charge)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingChargeForNonExistingBooking() throws Exception {
        Charge charge = new Charge();

        when(bookingDao.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/charges/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charge)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenCreateChargeThrowsDataAccessException() throws Exception {
        Booking booking = new Booking();
        Charge charge = new Charge();

        when(bookingDao.findById(1L)).thenReturn(Optional.of(booking));
        when(chargeDao.save(any(Charge.class))).thenThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")));

        mockMvc.perform(post("/api/charges/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charge)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateChargeSuccessfully() throws Exception {
        Charge existingCharge = new Charge();
        Charge chargeData = new Charge();
        chargeData.setDescription("Updated Description");
        chargeData.setAmount(new BigDecimal("50"));
        chargeData.setType(ChargeType.ACTIVIDAD);
        chargeData.setAppliedToInvoice(true);

        when(chargeDao.findById(1L)).thenReturn(Optional.of(existingCharge));
        when(chargeDao.save(any(Charge.class))).thenReturn(existingCharge);

        mockMvc.perform(put("/api/charges/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chargeData)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCharge() throws Exception {
        Charge chargeData = new Charge();

        when(chargeDao.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/charges/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chargeData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteChargeSuccessfully() throws Exception {
        when(chargeDao.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/charges/1"))
                .andExpect(status().isOk());

        verify(chargeDao).deleteById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCharge() throws Exception {
        when(chargeDao.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/charges/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenDeleteChargeThrowsDataAccessException() throws Exception {
        when(chargeDao.existsById(1L)).thenReturn(true);
        doThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause"))).when(chargeDao).deleteById(1L);

        mockMvc.perform(delete("/api/charges/1"))
                .andExpect(status().isInternalServerError());
    }
}