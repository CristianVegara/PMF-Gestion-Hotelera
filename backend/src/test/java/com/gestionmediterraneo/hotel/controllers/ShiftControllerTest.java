package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Shift;
import com.gestionmediterraneo.hotel.security.JwtUtils;
import com.gestionmediterraneo.hotel.services.IShiftService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShiftController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IShiftService shiftService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllShifts() throws Exception {
        List<Shift> shifts = Arrays.asList(new Shift(), new Shift());
        when(shiftService.findAll()).thenReturn(shifts);

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnErrorWhenFindAllFails() throws Exception {
        when(shiftService.findAll()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al realizar la consulta en la base de datos"));
    }

    @Test
    void shouldReturnShiftsByRange() throws Exception {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        
        when(shiftService.findShiftsByRange(start, end))
                .thenReturn(Collections.singletonList(new Shift()));

        mockMvc.perform(get("/api/shifts/range")
                .param("start", "2026-01-01")
                .param("end", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldCreateShift() throws Exception {
        Shift shift = new Shift();
        shift.setObservaciones("Prueba");

        when(shiftService.save(any(Shift.class))).thenReturn(shift);

        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDeleteShift() throws Exception {
        doNothing().when(shiftService).delete(anyLong());

        mockMvc.perform(delete("/api/shifts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El turno ha sido eliminado con éxito"));
    }

    @Test
    void shouldUpdateShiftWhenExists() throws Exception {
        Long id = 1L;
        Shift existingShift = new Shift();
        existingShift.setId(id);
        
        Shift updateDetails = new Shift();
        updateDetails.setObservaciones("Actualizado");

        when(shiftService.findAll()).thenReturn(Arrays.asList(existingShift));
        when(shiftService.save(any(Shift.class))).thenReturn(existingShift);

        mockMvc.perform(put("/api/shifts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateShiftDoesNotExist() throws Exception {
        Shift updateDetails = new Shift();
        when(shiftService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(put("/api/shifts/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());
    }
}