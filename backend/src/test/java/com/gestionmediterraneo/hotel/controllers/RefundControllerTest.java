package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IRefundDAO;
import com.gestionmediterraneo.hotel.entities.Refund;
import com.gestionmediterraneo.hotel.security.JwtUtils;

@WebMvcTest(controllers = RefundController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class RefundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRefundDAO refundDao;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRefundSuccessfully() throws Exception {
        Refund refund = new Refund();
        
        when(refundDao.save(any(Refund.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/refunds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refund)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Reembolso registrado con éxito"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenDatabaseFails() throws Exception {
        Refund refund = new Refund();

        when(refundDao.save(any(Refund.class))).thenThrow(new TransientDataAccessResourceException("Error"));

        mockMvc.perform(post("/api/refunds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refund)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al guardar reembolso"));
    }
}