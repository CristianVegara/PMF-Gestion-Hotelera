package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.gestionmediterraneo.hotel.daos.IScheduleDAO;
import com.gestionmediterraneo.hotel.entities.Schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private IScheduleDAO scheduleDao;

    @InjectMocks
    private ScheduleServiceImp scheduleService;

    @Test
    void shouldReturnAllSchedules() {
        List<Schedule> schedules = Arrays.asList(new Schedule(), new Schedule());
        when(scheduleDao.findAll()).thenReturn(schedules);

        List<Schedule> result = scheduleService.findAll();

        assertEquals(2, result.size());
        verify(scheduleDao, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoSchedulesExist() {
        when(scheduleDao.findAll()).thenReturn(List.of());

        List<Schedule> result = scheduleService.findAll();

        assertTrue(result.isEmpty());
        verify(scheduleDao, times(1)).findAll();
    }

    @Test
    void shouldSaveSchedule() {
        Schedule schedule = new Schedule();
        when(scheduleDao.save(schedule)).thenReturn(schedule);

        Schedule result = scheduleService.save(schedule);

        assertNotNull(result);
        verify(scheduleDao, times(1)).save(schedule);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Schedule schedule = new Schedule();
        when(scheduleDao.save(schedule)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            scheduleService.save(schedule);
        });
    }
}