package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Schedule;
import com.gestionmediterraneo.hotel.security.JwtUtils;
import com.gestionmediterraneo.hotel.services.IScheduleService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IScheduleService scheduleService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnSchedules() throws Exception {
        when(scheduleService.findAll())
                .thenReturn(Arrays.asList(new Schedule(), new Schedule()));

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateSchedule() throws Exception {
        Schedule schedule = new Schedule();

        when(scheduleService.save(any(Schedule.class))).thenReturn(schedule);

        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(schedule)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateSchedule() throws Exception {
        Schedule scheduleDetails = new Schedule();

        when(scheduleService.save(any(Schedule.class))).thenReturn(scheduleDetails);

        mockMvc.perform(put("/api/schedules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduleDetails)))
                .andExpect(status().isOk());
    }
}