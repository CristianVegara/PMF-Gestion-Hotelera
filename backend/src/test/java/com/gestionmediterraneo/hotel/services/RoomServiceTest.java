package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Room;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private IRoomDAO roomDao;

    @InjectMocks
    private RoomServiceImp roomService;

    @Test
    void shouldReturnAllRooms() {
        List<Room> rooms = Arrays.asList(new Room(), new Room());

        when(roomDao.findAll()).thenReturn(rooms);

        List<Room> result = roomService.findAll();

        assertEquals(2, result.size());
        verify(roomDao, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRoomsExist() {
        when(roomDao.findAll()).thenReturn(List.of());

        List<Room> result = roomService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveRoom() {
        Room room = new Room();

        when(roomDao.save(room)).thenReturn(room);

        Room result = roomService.save(room);

        assertNotNull(result);
        verify(roomDao, times(1)).save(room);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Room room = new Room();

        when(roomDao.save(room)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            roomService.save(room);
        });
    }

    @Test
    void shouldReturnRoomWhenIdExists() {
        Room room = new Room();
        room.setId(1L);

        when(roomDao.findById(1L)).thenReturn(Optional.of(room));

        Room result = roomService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(roomDao).findById(1L);
    }

    @Test
    void shouldReturnNullWhenRoomDoesNotExist() {
        when(roomDao.findById(1L)).thenReturn(Optional.empty());

        Room result = roomService.findById(1L);

        assertNull(result);
        verify(roomDao).findById(1L);
    }

    @Test
    void shouldDeleteRoom() {
        Room room = new Room();

        roomService.delete(room);

        verify(roomDao, times(1)).delete(room);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        Room room = new Room();

        doThrow(new RuntimeException("DB error")).when(roomDao).delete(room);

        assertThrows(RuntimeException.class, () -> {
            roomService.delete(room);
        });
    }
}