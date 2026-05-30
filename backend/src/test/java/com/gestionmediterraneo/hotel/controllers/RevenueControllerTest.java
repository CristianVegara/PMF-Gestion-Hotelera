package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.gestionmediterraneo.hotel.security.JwtUtils;
import com.gestionmediterraneo.hotel.services.RevenueReportService;

@WebMvcTest(controllers = RevenueController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class RevenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RevenueReportService revenueReportService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void shouldReturnRevenueReportSuccessfully() throws Exception {
        Map<String, Object> report = new HashMap<>();

        when(revenueReportService.calculateRevenue(
                any(LocalDate.class), any(LocalDate.class), any(), any(), any()))
                .thenReturn(report);

        mockMvc.perform(get("/api/reports/revenue")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInternalServerErrorWhenCalculationFails() throws Exception {
        when(revenueReportService.calculateRevenue(
                any(LocalDate.class), any(LocalDate.class), any(), any(), any()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/reports/revenue")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al generar el reporte de ingresos"));
    }
}