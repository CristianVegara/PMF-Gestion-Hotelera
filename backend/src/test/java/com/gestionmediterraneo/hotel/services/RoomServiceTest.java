package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private IRoomDAO roomDao;

    @Mock
    private IBookingDAO bookingDao;

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

        assertThrows(RuntimeException.class, () -> roomService.save(room));
    }

    @Test
    void shouldReturnRoomWhenIdExists() {
        Room room = new Room();
        room.setId(1L);
        when(roomDao.findById(1L)).thenReturn(Optional.of(room));

        Room result = roomService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldReturnNullWhenRoomDoesNotExist() {
        when(roomDao.findById(1L)).thenReturn(Optional.empty());

        Room result = roomService.findById(1L);

        assertNull(result);
    }

    @Test
    void shouldDeleteRoom() {
        Room room = new Room();
        roomService.delete(room);
        verify(roomDao, times(1)).delete(room);
    }

    @Test
    void shouldReturnPriceByTypeAndDate() {
        Room room = new Room();
        room.setType(RoomType.DOBLE);
        room.setPrice(100.0);
        
        when(roomDao.findAll()).thenReturn(Arrays.asList(room));
        when(bookingDao.countOccupiedByDate(any(LocalDate.class))).thenReturn(0L);
        when(bookingDao.countOccupiedByDateAndType(any(LocalDate.class), eq(RoomType.DOBLE))).thenReturn(0L);

        Double price = roomService.getPriceByTypeAndDate(RoomType.DOBLE, LocalDate.of(2026, 7, 1));

        assertNotNull(price);
        assertTrue(price > 0);
    }

    @Test
    void shouldReturnDynamicPriceForRoom() {
        Room room = new Room();
        room.setId(1L);
        room.setPrice(100.0);
        room.setType(RoomType.INDIVIDUAL);

        when(roomDao.findById(1L)).thenReturn(Optional.of(room));
        when(roomDao.findAll()).thenReturn(Arrays.asList(room));
        when(bookingDao.countOccupiedByDate(any(LocalDate.class))).thenReturn(0L);
        when(bookingDao.countOccupiedByDateAndType(any(LocalDate.class), eq(RoomType.INDIVIDUAL))).thenReturn(0L);

        Room result = roomService.getRoomWithDynamicPrice(1L, LocalDate.of(2026, 7, 1));

        assertNotNull(result);
        assertTrue(result.getDynamicPrice() > 0);
    }

    @Test
    void shouldReturnAllRoomsWithDynamicPricingApplied() {
        Room room = new Room();
        room.setPrice(100.0);
        room.setType(RoomType.SUITE);

        when(roomDao.findAll()).thenReturn(Arrays.asList(room));
        when(bookingDao.countOccupiedByDate(any(LocalDate.class))).thenReturn(0L);
        when(bookingDao.countOccupiedByDateAndType(any(LocalDate.class), eq(RoomType.SUITE))).thenReturn(0L);

        List<Room> result = roomService.getAllRoomsWithDynamicPrice(LocalDate.of(2026, 7, 1));

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getDynamicPrice());
    }
}