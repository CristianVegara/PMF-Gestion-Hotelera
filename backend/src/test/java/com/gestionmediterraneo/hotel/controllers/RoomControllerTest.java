package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.services.IRoomService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnRooms() throws Exception {
        when(roomService.findAll())
                .thenReturn(Arrays.asList(new Room(), new Room()));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnRoomByIdWhenExists() throws Exception {
        Long id = 1L;
        Room room = new Room();
        room.setId(id);

        when(roomService.findById(id)).thenReturn(room);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenRoomDoesNotExist() throws Exception {
        when(roomService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateRoom() throws Exception {
        Room room = new Room();
        room.setNumber("101");
        room.setType("Double");
        room.setPrice(150.0);
        room.setStatus("Available");

        when(roomService.save(any(Room.class))).thenReturn(room);

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("La habitación ha sido creada con éxito"));
    }

    @Test
    void shouldReturnBadRequestWhenFieldsAreInvalidOnCreate() throws Exception {
        Room invalidRoom = new Room();
        invalidRoom.setNumber(""); 

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRoom)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void shouldUpdateRoomWhenExists() throws Exception {
        Long id = 1L;
        Room existingRoom = new Room();
        existingRoom.setId(id);

        Room updatedRoom = new Room();
        updatedRoom.setNumber("102");
        updatedRoom.setType("Suite");
        updatedRoom.setPrice(250.0);
        updatedRoom.setStatus("Available");

        when(roomService.findById(id)).thenReturn(existingRoom);
        when(roomService.save(any(Room.class))).thenReturn(updatedRoom);

        mockMvc.perform(put("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRoom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("La habitación ha sido actualizada con éxito"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingRoom() throws Exception {
        Room roomData = new Room();
        roomData.setNumber("105");
        roomData.setType("Single");
        roomData.setPrice(100.0);
        roomData.setStatus("Available");

        when(roomService.findById(1L)).thenReturn(null);

        mockMvc.perform(put("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenUpdateDataIsInvalid() throws Exception {
        Room invalidRoom = new Room();
        invalidRoom.setNumber("105");

        mockMvc.perform(put("/api/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRoom)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldDeleteRoomWhenExists() throws Exception {
        Room room = new Room();
        room.setId(1L);

        when(roomService.findById(1L)).thenReturn(room);

        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingRoom() throws Exception {
        when(roomService.findById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNotFound());
    }
}