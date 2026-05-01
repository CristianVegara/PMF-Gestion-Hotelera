package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IActivityService activityService;

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
    void shouldReturnActivityByIdWhenExists() throws Exception {
        Long id = 1L;
        Activity activity = new Activity();
        activity.setId(id);

        when(activityService.findById(id)).thenReturn(activity);

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
        Activity activity = new Activity();
        activity.setDescripcion(""); 

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateActivityWhenExists() throws Exception {
        Activity existingActivity = new Activity();
        existingActivity.setId(1L);

        Activity updatedActivity = new Activity();
        updatedActivity.setDescripcion("Nueva Descripcion");
        updatedActivity.setPrecio(50.0);

        when(activityService.findById(1L)).thenReturn(existingActivity);
        when(activityService.save(any(Activity.class))).thenReturn(updatedActivity);

        mockMvc.perform(put("/api/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isOk()); 
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingActivity() throws Exception {
        Activity updatedActivity = new Activity();
        updatedActivity.setDescripcion("Test");

        when(activityService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/activities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isNotFound());
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
}