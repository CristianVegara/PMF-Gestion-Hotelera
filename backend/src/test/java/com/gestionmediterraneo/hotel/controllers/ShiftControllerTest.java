package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Shift;
import com.gestionmediterraneo.hotel.services.IShiftService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShiftController.class)
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IShiftService shiftService;

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
    void shouldReturnShiftsByRange() throws Exception {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        
        when(shiftService.findShiftsByRange(start, end))
                .thenReturn(Arrays.asList(new Shift()));

        mockMvc.perform(get("/api/shifts/range")
                .param("start", "2023-01-01")
                .param("end", "2023-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldCreateShift() throws Exception {
        Shift shift = new Shift();
        shift.setFecha(LocalDate.now());
        shift.setObservaciones("Prueba");

        when(shiftService.save(any(Shift.class))).thenReturn(shift);

        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shift)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateShiftWhenExists() throws Exception {
        Long id = 1L;
        Shift existingShift = new Shift();
        existingShift.setId(id);
        
        Shift updateDetails = new Shift();
        updateDetails.setFecha(LocalDate.now());
        updateDetails.setObservaciones("Actualizado");

        when(shiftService.findAll()).thenReturn(Arrays.asList(existingShift));
        when(shiftService.save(any(Shift.class))).thenReturn(existingShift);

        mockMvc.perform(put("/api/shifts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInternalServerErrorWhenUpdateShiftNotFound() throws Exception {
        Long id = 99L;
        Shift updateDetails = new Shift();
        
        when(shiftService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(put("/api/shifts/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isInternalServerError()); 
    }

    @Test
    void shouldDeleteShift() throws Exception {
        doNothing().when(shiftService).delete(anyLong());

        mockMvc.perform(delete("/api/shifts/1"))
                .andExpect(status().isOk());
    }
}