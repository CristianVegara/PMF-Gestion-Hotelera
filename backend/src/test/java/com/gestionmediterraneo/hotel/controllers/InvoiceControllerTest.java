package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IClientDAO;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.services.AuditLogService;
import com.gestionmediterraneo.hotel.services.BillingService;
import com.gestionmediterraneo.hotel.services.InvoiceService;
import com.gestionmediterraneo.hotel.services.LoyaltyService;
import com.gestionmediterraneo.hotel.services.LoyaltyTier;
import com.gestionmediterraneo.hotel.services.RevenueReportService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private IClientDAO clientDao;

    @MockBean
    private LoyaltyService loyaltyService;

    @MockBean
    private BillingService billingService;

    @MockBean
    private RevenueReportService revenueReportService;

    @MockBean
    private AuditLogService auditLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnInvoices() throws Exception {
        when(invoiceService.findAllSorted(anyString(), anyString()))
                .thenReturn(Arrays.asList(new Invoice(), new Invoice()));

        mockMvc.perform(get("/api/invoice"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInvoiceByIdWhenExists() throws Exception {
        Long id = 1L;
        Invoice invoice = new Invoice();
        invoice.setId(id);

        when(invoiceService.findById(id)).thenReturn(Optional.of(invoice));

        mockMvc.perform(get("/api/invoice/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenInvoiceDoesNotExist() throws Exception {
        when(invoiceService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/invoice/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateInvoice() throws Exception {
        Client cliente = new Client();
        cliente.setId(1L);
        cliente.setDiscounts(new ArrayList<>());

        Invoice invoice = new Invoice();
        invoice.setCliente(cliente);
        invoice.setConcepto("Reserva Habitación");
        invoice.setNoches(2);
        invoice.setPrecio(new BigDecimal("100.00"));

        when(clientDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(loyaltyService.calculateTier(any(Client.class)))
                .thenReturn(new LoyaltyTier("Bronze", 5, 3, 3));
        when(invoiceService.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.loyaltyRank").value("Bronze"))
                .andExpect(jsonPath("$.data.discountPercentage").value(5))
                .andExpect(jsonPath("$.data.discountAmount").value(10.0))
                .andExpect(jsonPath("$.data.subtotal").value(190.0))
                .andExpect(jsonPath("$.data.iva").value(19.0))
                .andExpect(jsonPath("$.data.total").value(209.0));
    }

    @Test
    void shouldReturnBadRequestWhenClientDoesNotExistOnCreate() throws Exception {
        Client cliente = new Client();
        cliente.setId(99L);

        Invoice invoice = new Invoice();
        invoice.setCliente(cliente);

        when(clientDao.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateInvoiceWhenExists() throws Exception {
        Long id = 1L;
        Client cliente = new Client();
        cliente.setId(1L);
        cliente.setDiscounts(new ArrayList<>());

        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(id);

        Invoice updateData = new Invoice();
        updateData.setCliente(cliente);
        updateData.setConcepto("Update");
        updateData.setNoches(3);
        updateData.setPrecio(new BigDecimal("150.00"));

        when(invoiceService.findById(id)).thenReturn(Optional.of(existingInvoice));
        when(clientDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(loyaltyService.calculateTier(any(Client.class)))
                .thenReturn(new LoyaltyTier("Silver", 10, 5, 6));
        when(invoiceService.save(any(Invoice.class))).thenReturn(existingInvoice);

        mockMvc.perform(put("/api/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingInvoice() throws Exception {
        Invoice invoice = new Invoice();
        when(invoiceService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteInvoiceWhenExists() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(invoiceService.findById(1L)).thenReturn(Optional.of(invoice));

        mockMvc.perform(delete("/api/invoice/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingInvoice() throws Exception {
        when(invoiceService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/invoice/1"))
                .andExpect(status().isNotFound());
    }
}
