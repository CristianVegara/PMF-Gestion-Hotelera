package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.services.AuditLogService;
import com.gestionmediterraneo.hotel.services.IUserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    void shouldReturnListOfUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user");

        List<User> allUsers = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(allUsers);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
        when(userService.findAll()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError());
    }
}
