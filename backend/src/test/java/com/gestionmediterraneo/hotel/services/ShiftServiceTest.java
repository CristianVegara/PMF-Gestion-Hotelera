package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.gestionmediterraneo.hotel.daos.IShiftDAO;
import com.gestionmediterraneo.hotel.entities.Shift;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private IShiftDAO shiftDao;

    @InjectMocks
    private ShiftServiceImp shiftService;

    @Test
    void shouldReturnAllShifts() {
        List<Shift> shifts = Arrays.asList(new Shift(), new Shift());
        when(shiftDao.findAll()).thenReturn(shifts);

        List<Shift> result = shiftService.findAll();

        assertEquals(2, result.size());
        verify(shiftDao, times(1)).findAll();
    }

    @Test
    void shouldReturnShiftsByRange() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(5);
        List<Shift> shifts = Arrays.asList(new Shift());
        when(shiftDao.findByFechaBetween(start, end)).thenReturn(shifts);

        List<Shift> result = shiftService.findShiftsByRange(start, end);

        assertEquals(1, result.size());
        verify(shiftDao, times(1)).findByFechaBetween(start, end);
    }

    @Test
    void shouldSaveShift() {
        Shift shift = new Shift();
        when(shiftDao.save(shift)).thenReturn(shift);

        Shift result = shiftService.save(shift);

        assertNotNull(result);
        verify(shiftDao, times(1)).save(shift);
    }

    @Test
    void shouldDeleteShift() {
        Long id = 1L;

        shiftService.delete(id);

        verify(shiftDao, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        Long id = 1L;
        doThrow(new RuntimeException("DB error")).when(shiftDao).deleteById(id);

        assertThrows(RuntimeException.class, () -> {
            shiftService.delete(id);
        });
    }
}