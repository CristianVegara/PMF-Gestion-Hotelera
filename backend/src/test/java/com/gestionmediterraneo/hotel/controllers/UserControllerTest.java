package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.security.JwtUtils;
import com.gestionmediterraneo.hotel.services.IUserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(Arrays.asList(new User(), new User()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnUserByUsername() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.findByUsername("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/users/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUser() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setPasswordHash("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El usuario ha sido creado con éxito"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        String username = "existingUser";
        User existingUser = new User();
        existingUser.setUsername(username);
        
        User updateDetails = new User();
        updateDetails.setUsername(username);
        updateDetails.setPasswordHash("newPassword");

        when(userService.findByUsername(username)).thenReturn(existingUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");
        when(userService.save(any(User.class))).thenReturn(existingUser);

        mockMvc.perform(put("/api/users/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("El usuario ha sido actualizado con éxito"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        String username = "userToDelete";
        User user = new User();
        user.setUsername(username);

        when(userService.findByUsername(username)).thenReturn(user);
        doNothing().when(userService).delete(user);

        mockMvc.perform(delete("/api/users/" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("El usuario ha sido eliminado con éxito"));
    }

    @Test
    void shouldReturnInternalServerErrorOnDatabaseException() throws Exception {
        when(userService.findAll()).thenThrow(new DataAccessException("DB Error") {
            private static final long serialVersionUID = 1L;
        });

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al realizar la consulta en la base de datos"));
    }
}