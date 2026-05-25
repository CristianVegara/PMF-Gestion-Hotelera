package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.daos.IPaymentDAO;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.entities.Payment;
import com.gestionmediterraneo.hotel.security.JwtUtils;

@WebMvcTest(controllers = PaymentController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPaymentDAO paymentDao;

    @MockBean
    private IInvoiceDAO invoiceDao;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setCliente(new Client());

        Payment payment = new Payment();
        payment.setInvoice(invoice);

        when(invoiceDao.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentDao.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Pago registrado con éxito"));
    }

    @Test
    void shouldReturnNotFoundWhenInvoiceDoesNotExist() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(99L);
        Payment payment = new Payment();
        payment.setInvoice(invoice);

        when(invoiceDao.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Factura no encontrada"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenDatabaseFails() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        Payment payment = new Payment();
        payment.setInvoice(invoice);

        when(invoiceDao.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentDao.save(any(Payment.class))).thenThrow(new TransientDataAccessResourceException("Error"));

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al guardar pago"));
    }
}