package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.gestionmediterraneo.hotel.daos.IUserDAO;
import com.gestionmediterraneo.hotel.entities.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserDAO userDao;

    @InjectMocks
    private UserServiceImp userService;

    @Test
    void shouldReturnAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());

        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        when(userDao.findAll()).thenReturn(List.of());

        List<User> result = userService.findAll();

        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionWhenFindAllFails() {
        when(userDao.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            userService.findAll();
        });
    }
}