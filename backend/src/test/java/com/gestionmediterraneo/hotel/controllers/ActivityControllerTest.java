package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Activity;
import com.gestionmediterraneo.hotel.services.IActivityService;
import com.gestionmediterraneo.hotel.security.JwtUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ActivityController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IActivityService activityService;

    @MockBean 
    private JwtUtils jwtUtils; 

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnActivities() throws Exception {
        when(activityService.findAllSorted("id", "asc"))
                .thenReturn(Arrays.asList(new Activity(), new Activity()));

        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetActivitiesFails() throws Exception {
        DataAccessException mockException = mock(DataAccessException.class);
        when(mockException.getMessage()).thenReturn("Database error");
        when(mockException.getMostSpecificCause()).thenReturn(new RuntimeException("Root cause"));
        
        when(activityService.findAllSorted("id", "asc")).thenThrow(mockException);

        mockMvc.perform(get("/api/activities"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnActivityByIdWhenExists() throws Exception {
        Activity activity = new Activity();
        activity.setId(1L);

        when(activityService.findById(1L)).thenReturn(activity);

        mockMvc.perform(get("/api/activities/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenActivityDoesNotExist() throws Exception {
        when(activityService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/activities/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenShowFails() throws Exception {
        DataAccessException mockException = mock(DataAccessException.class);
        when(mockException.getMessage()).thenReturn("Database error");
        when(mockException.getMostSpecificCause()).thenReturn(new RuntimeException("Root cause"));
        
        when(activityService.findById(1L)).thenThrow(mockException);

        mockMvc.perform(get("/api/activities/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldFilterActivitiesByDate() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(activityService.findByFechaComienzoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(new Activity()));

        mockMvc.perform(get("/api/activities/filter")
                .param("fechaInicio", start.toString())
                .param("fechaFin", end.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInternalServerErrorWhenFilterFails() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(activityService.findByFechaComienzoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Generic filter error"));

        mockMvc.perform(get("/api/activities/filter")
                .param("fechaInicio", start.toString())
                .param("fechaFin", end.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCreateActivity() throws Exception {
        Activity activity = new Activity();
        activity.setDescripcion("Senderismo");
        activity.setPrecio(20.0);
        activity.setFechaComienzo(LocalDateTime.now());
        activity.setFechaFin(LocalDateTime.now().plusHours(2));

        when(activityService.save(any(Activity.class))).thenReturn(activity);

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsMissingOnCreate() throws Exception {
        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerErrorWhenCreateFails() throws Exception {
        Activity activity = new Activity();
        activity.setDescripcion("Senderismo");
        activity.setPrecio(20.0);
        activity.setFechaComienzo(LocalDateTime.now());
        activity.setFechaFin(LocalDateTime.now().plusHours(2));

        DataAccessException mockException = mock(DataAccessException.class);
        when(mockException.getMessage()).thenReturn("Database error");
        when(mockException.getMostSpecificCause()).thenReturn(new RuntimeException("Root cause"));

        when(activityService.save(any(Activity.class))).thenThrow(mockException);

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateActivityWhenExists() throws Exception {
        Activity existingActivity = new Activity();
        existingActivity.setId(1L);

        Activity updatedActivity = new Activity();
        updatedActivity.setDescripcion("Nueva Descripcion");
        updatedActivity.setPrecio(50.0);
        updatedActivity.setFechaComienzo(LocalDateTime.now());
        updatedActivity.setFechaFin(LocalDateTime.now().plusHours(2));

        when(activityService.findById(1L)).thenReturn(existingActivity);
        when(activityService.save(any(Activity.class))).thenReturn(updatedActivity);

        mockMvc.perform(put("/api/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isOk()); 
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsMissingOnUpdate() throws Exception {
        mockMvc.perform(put("/api/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingActivity() throws Exception {
        Activity updatedActivity = new Activity();
        updatedActivity.setDescripcion("Test");
        updatedActivity.setPrecio(20.0);
        updatedActivity.setFechaComienzo(LocalDateTime.now());
        updatedActivity.setFechaFin(LocalDateTime.now().plusHours(2));

        when(activityService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenUpdateFails() throws Exception {
        Activity existingActivity = new Activity();
        existingActivity.setId(1L);

        Activity updatedActivity = new Activity();
        updatedActivity.setDescripcion("Nueva Descripcion");
        updatedActivity.setPrecio(20.0);
        updatedActivity.setFechaComienzo(LocalDateTime.now());
        updatedActivity.setFechaFin(LocalDateTime.now().plusHours(2));

        DataAccessException mockException = mock(DataAccessException.class);
        when(mockException.getMessage()).thenReturn("Database error");
        when(mockException.getMostSpecificCause()).thenReturn(new RuntimeException("Root cause"));

        when(activityService.findById(1L)).thenReturn(existingActivity);
        when(activityService.save(any(Activity.class))).thenThrow(mockException);

        mockMvc.perform(put("/api/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldDeleteActivityWhenExists() throws Exception {
        Activity activity = new Activity();
        activity.setId(1L);

        when(activityService.findById(1L)).thenReturn(activity);

        mockMvc.perform(delete("/api/activities/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingActivity() throws Exception {
        when(activityService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/activities/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenDeleteFails() throws Exception {
        Activity activity = new Activity();
        activity.setId(1L);

        DataAccessException mockException = mock(DataAccessException.class);
        when(mockException.getMessage()).thenReturn("Database error");
        when(mockException.getMostSpecificCause()).thenReturn(new RuntimeException("Root cause"));

        when(activityService.findById(1L)).thenReturn(activity);
        doThrow(mockException).when(activityService).delete(any(Activity.class));

        mockMvc.perform(delete("/api/activities/1"))
                .andExpect(status().isInternalServerError());
    }
}