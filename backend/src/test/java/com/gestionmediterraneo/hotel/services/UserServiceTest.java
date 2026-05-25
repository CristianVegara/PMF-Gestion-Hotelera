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
    void shouldReturnUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        when(userDao.findByUsername("testuser")).thenReturn(user);

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userDao, times(1)).findByUsername("testuser");
    }

    @Test
    void shouldSaveUser() {
        User user = new User();
        when(userDao.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertNotNull(result);
        verify(userDao, times(1)).save(user);
    }

    @Test
    void shouldDeleteUser() {
        User user = new User();
        userService.delete(user);
        verify(userDao, times(1)).delete(user);
    }

    @Test
    void shouldFindUserByEmployeeId() {
        User user = new User();
        when(userDao.findByEmployee_Id(1L)).thenReturn(user);

        User result = userService.findByEmployee_Id(1L);

        assertNotNull(result);
        verify(userDao, times(1)).findByEmployee_Id(1L);
    }
}