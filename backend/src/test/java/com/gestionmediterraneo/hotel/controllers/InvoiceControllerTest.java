package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.daos.IClientDAO;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.security.JwtUtils;
import com.gestionmediterraneo.hotel.services.BillingService;
import com.gestionmediterraneo.hotel.services.InvoiceGenerationRequest;
import com.gestionmediterraneo.hotel.services.InvoiceService;
import com.gestionmediterraneo.hotel.services.LoyaltyService;
import com.gestionmediterraneo.hotel.services.LoyaltyTier;
import com.gestionmediterraneo.hotel.services.RevenueReportService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = InvoiceController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
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
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnInvoices() throws Exception {
        when(invoiceService.findAllSorted(anyString(), anyString()))
                .thenReturn(Arrays.asList(new Invoice(), new Invoice()));

        mockMvc.perform(get("/api/invoice")
                .param("sortBy", "id")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetInvoicesFails() throws Exception {
        when(invoiceService.findAllSorted(anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/invoice"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al obtener facturas"));
    }

    @Test
    void shouldGenerateInvoiceSuccessfully() throws Exception {
        InvoiceGenerationRequest request = new InvoiceGenerationRequest();
        Invoice invoice = new Invoice();

        when(billingService.generateInvoice(any(InvoiceGenerationRequest.class))).thenReturn(invoice);

        mockMvc.perform(post("/api/invoice/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Factura generada con éxito"));
    }

    @Test
    void shouldReturnBadRequestWhenGenerateInvoiceThrowsIllegalArgumentException() throws Exception {
        InvoiceGenerationRequest request = new InvoiceGenerationRequest();

        when(billingService.generateInvoice(any(InvoiceGenerationRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid booking ID"));

        mockMvc.perform(post("/api/invoice/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Invalid booking ID"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenGenerateInvoiceFails() throws Exception {
        InvoiceGenerationRequest request = new InvoiceGenerationRequest();

        when(billingService.generateInvoice(any(InvoiceGenerationRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/invoice/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al generar la factura"));
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Factura ID: 1 no existe"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenFindByIdThrowsDataAccessException() throws Exception {
        when(invoiceService.findById(1L))
                .thenThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")));

        mockMvc.perform(get("/api/invoice/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al consultar la base de datos"));
    }

    @Test
    void shouldReturnRevenueReport() throws Exception {
        Map<String, Object> reportResponse = new HashMap<>();

        when(revenueReportService.calculateRevenue(
                any(LocalDate.class), any(LocalDate.class), any(), any(), any()))
                .thenReturn(reportResponse);

        mockMvc.perform(get("/api/invoice/report")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldReturnInternalServerErrorWhenRevenueReportFails() throws Exception {
        when(revenueReportService.calculateRevenue(
                any(LocalDate.class), any(LocalDate.class), any(), any(), any()))
                .thenThrow(new RuntimeException("Calculation failed"));

        mockMvc.perform(get("/api/invoice/report")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al generar el informe"));
    }

    @Test
    void shouldReturnClientExpensesSuccessfully() throws Exception {
        Client client = new Client();
        client.setId(1L);
        Invoice invoice = new Invoice();
        invoice.setTotal(new BigDecimal("150.00"));

        when(clientDao.findById(1L)).thenReturn(Optional.of(client));
        when(invoiceService.findByClientBookingDateRange(
                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(invoice));

        mockMvc.perform(get("/api/invoice/client/1/expenses")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent").value(150.00))
                .andExpect(jsonPath("$.invoiceCount").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenGettingExpensesForNonExistingClient() throws Exception {
        when(clientDao.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/invoice/client/1/expenses")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenClientExpensesFails() throws Exception {
        when(clientDao.findById(1L)).thenThrow(new RuntimeException("Database failure"));

        mockMvc.perform(get("/api/invoice/client/1/expenses")
                .param("from", "2026-01-01")
                .param("to", "2026-01-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al consultar gastos del cliente"));
    }

    @Test
    void shouldCreateInvoice() throws Exception {
        Client cliente = new Client();
        cliente.setId(1L);

        Invoice invoice = new Invoice();
        invoice.setCliente(cliente);
        invoice.setConcepto("Reserva Habitación");
        invoice.setNoches(2);
        invoice.setPrecio(new BigDecimal("100.00"));

        when(clientDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(loyaltyService.calculateTier(any(Client.class)))
                .thenReturn(new LoyaltyTier("Bronze", 5.0, 3, 3));
        when(invoiceService.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Factura creada con éxito"))
                .andExpect(jsonPath("$.data.loyaltyRank").value("Bronze"))
                .andExpect(jsonPath("$.data.discountPercentage").value(5.0))
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cliente no existe"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenCreateInvoiceThrowsDataAccessException() throws Exception {
        Client cliente = new Client();
        cliente.setId(1L);

        Invoice invoice = new Invoice();
        invoice.setCliente(cliente);
        invoice.setConcepto("Reserva de prueba");
        invoice.setNoches(2);
        invoice.setPrecio(new BigDecimal("100.00"));

        when(clientDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(loyaltyService.calculateTier(any(Client.class)))
                .thenReturn(new LoyaltyTier("Bronze", 5.0, 3, 3));
        when(invoiceService.save(any(Invoice.class)))
                .thenThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")));

        mockMvc.perform(post("/api/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al guardar factura"));
    }

    @Test
    void shouldUpdateInvoiceWhenExists() throws Exception {
        Long id = 1L;
        Client cliente = new Client();
        cliente.setId(1L);

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
                .thenReturn(new LoyaltyTier("Silver", 10.0, 5, 6));
        when(invoiceService.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Factura actualizada"))
                .andExpect(jsonPath("$.data.discountPercentage").value(10.0));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingInvoice() throws Exception {
        Client cliente = new Client();
        cliente.setId(1L);
        Invoice invoice = new Invoice();
        invoice.setCliente(cliente);

        when(invoiceService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoice)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Factura no existe"));
    }

    @Test
    void shouldReturnBadRequestWhenClientDoesNotExistOnUpdate() throws Exception {
        Long id = 1L;
        Client cliente = new Client();
        cliente.setId(99L);

        Invoice existingInvoice = new Invoice();
        existingInvoice.setId(id);

        Invoice updateData = new Invoice();
        updateData.setCliente(cliente);

        when(invoiceService.findById(id)).thenReturn(Optional.of(existingInvoice));
        when(clientDao.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Cliente no existe"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenUpdateInvoiceThrowsDataAccessException() throws Exception {
        Long id = 1L;
        Client cliente = new Client();
        cliente.setId(1L);

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
                .thenReturn(new LoyaltyTier("Silver", 10.0, 5, 6));
        when(invoiceService.save(any(Invoice.class)))
                .thenThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")));

        mockMvc.perform(put("/api/invoice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al actualizar factura"));
    }

    @Test
    void shouldDeleteInvoiceWhenExists() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(invoiceService.findById(1L)).thenReturn(Optional.of(invoice));

        mockMvc.perform(delete("/api/invoice/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Factura eliminada"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingInvoice() throws Exception {
        when(invoiceService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/invoice/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Factura no existe"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenDeleteInvoiceThrowsDataAccessException() throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(invoiceService.findById(1L)).thenReturn(Optional.of(invoice));
        org.mockito.Mockito.doThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")))
                .when(invoiceService).delete(1L);

        mockMvc.perform(delete("/api/invoice/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al eliminar factura"));
    }
}