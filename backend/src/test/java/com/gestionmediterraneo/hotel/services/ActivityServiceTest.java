package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.daos.IActivityDAO;
import com.gestionmediterraneo.hotel.entities.Activity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private IActivityDAO activityDao;

    @InjectMocks
    private ActivityServiceImp activityService;

    @Test
    void shouldReturnAllActivities() {
        List<Activity> activities = Arrays.asList(new Activity(), new Activity());

        when(activityDao.findAll()).thenReturn(activities);

        List<Activity> result = activityService.findAll();

        assertEquals(2, result.size());
        verify(activityDao, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoActivitiesExist() {
        when(activityDao.findAll()).thenReturn(List.of());

        List<Activity> result = activityService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveActivity() {
        Activity activity = new Activity();

        when(activityDao.save(activity)).thenReturn(activity);

        Activity result = activityService.save(activity);

        assertNotNull(result);
        verify(activityDao, times(1)).save(activity);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Activity activity = new Activity();

        when(activityDao.save(activity)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            activityService.save(activity);
        });
    }

    @Test
    void shouldReturnActivityWhenIdExists() {
        Activity activity = new Activity();
        activity.setId(1L);

        when(activityDao.findById(1L)).thenReturn(Optional.of(activity));

        Activity result = activityService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(activityDao).findById(1L);
    }

    @Test
    void shouldReturnNullWhenActivityDoesNotExist() {
        when(activityDao.findById(1L)).thenReturn(Optional.empty());

        Activity result = activityService.findById(1L);

        assertNull(result);
        verify(activityDao).findById(1L);
    }

    @Test
    void shouldDeleteActivity() {
        Activity activity = new Activity();

        Activity result = activityService.delete(activity);

        verify(activityDao, times(1)).delete(activity);
        assertEquals(activity, result);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        Activity activity = new Activity();

        doThrow(new RuntimeException("DB error")).when(activityDao).delete(activity);

        assertThrows(RuntimeException.class, () -> {
            activityService.delete(activity);
        });
    }

    @Test
    void shouldReturnSortedActivitiesAsc() {
        List<Activity> activities = Arrays.asList(new Activity());

        when(activityDao.findAll(any(Sort.class))).thenReturn(activities);

        List<Activity> result = activityService.findAllSorted("id", "asc");

        assertEquals(1, result.size());
        verify(activityDao).findAll(any(Sort.class));
    }

    @Test
    void shouldReturnSortedActivitiesDesc() {
        List<Activity> activities = Arrays.asList(new Activity());

        when(activityDao.findAll(any(Sort.class))).thenReturn(activities);

        List<Activity> result = activityService.findAllSorted("id", "desc");

        assertEquals(1, result.size());
        verify(activityDao).findAll(any(Sort.class));
    }

    @Test
    void shouldReturnActivitiesBetweenDates() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Activity> activities = Arrays.asList(new Activity());

        when(activityDao.findByFechaComienzoBetween(start, end)).thenReturn(activities);

        List<Activity> result = activityService.findByFechaComienzoBetween(start, end);

        assertEquals(1, result.size());
        verify(activityDao).findByFechaComienzoBetween(start, end);
    }
}