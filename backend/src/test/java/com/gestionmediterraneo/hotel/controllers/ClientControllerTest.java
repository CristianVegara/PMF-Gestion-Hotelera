package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.services.IClientService;
import com.gestionmediterraneo.hotel.security.JwtUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ClientController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClientService clientService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnClients() throws Exception {
        when(clientService.findAllSorted("id", "asc"))
                .thenReturn(Arrays.asList(new Client(), new Client()));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientByIdWhenExists() throws Exception {
        Long id = 1L;
        Client client = new Client();
        client.setId(id);       

        when(clientService.findById(id)).thenReturn(client);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        when(clientService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateClient() throws Exception {
        Client client = new Client();
        client.setNombre("Nombre");
        client.setDni("DNI");
        client.setTelefono("telefono");
        client.setCorreo("correo@email.com");

        when(clientService.save(any(Client.class))).thenReturn(client);

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated());
    }
    
    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        Client client = new Client();
        client.setNombre("");
        client.setDni("");
        client.setTelefono("");
        client.setCorreo("");

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteClientWhenExists() throws Exception {
        Client client = new Client();
        client.setId(1L);

        when(clientService.findById(1L)).thenReturn(client);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingClient() throws Exception {
        when(clientService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldUpdateClientWhenExists() throws Exception {
        Client existingClient = new Client();
        existingClient.setId(1L);

        Client updatedClient = new Client();
        updatedClient.setNombre("Updated Name");
        updatedClient.setDni("12345678A");
        updatedClient.setTelefono("999999999");
        updatedClient.setCorreo("updated@email.com");

        when(clientService.findById(1L)).thenReturn(existingClient);
        when(clientService.save(any(Client.class))).thenReturn(updatedClient);

        mockMvc.perform(put("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingClient() throws Exception {
        Client updatedClient = new Client();
        updatedClient.setNombre("Updated Name");
        updatedClient.setDni("Updated id");
        updatedClient.setTelefono("updated phone++");
        updatedClient.setCorreo("updated@email.com");

        when(clientService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldReturnBadRequestWhenUpdateDataIsInvalid() throws Exception {
        Client invalidClient = new Client();
        invalidClient.setNombre(""); 
        invalidClient.setDni("");     
        invalidClient.setTelefono(""); 
        invalidClient.setCorreo("invalid-email"); 

        mockMvc.perform(put("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetClientsFails() throws Exception {
        when(clientService.findAllSorted("id", "asc")).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isInternalServerError());
    }
}