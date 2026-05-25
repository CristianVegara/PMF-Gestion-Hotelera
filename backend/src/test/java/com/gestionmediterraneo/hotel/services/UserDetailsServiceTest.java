package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.gestionmediterraneo.hotel.daos.IUserDAO;
import com.gestionmediterraneo.hotel.entities.User;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private IUserDAO userDao;

    @InjectMocks
    private UserDetailsServiceImp userDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        String username = "admin";
        User user = new User();
        user.setUsername(username);
        
        when(userDao.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        verify(userDao, times(1)).findByUsername(username);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String username = "unknown";
        when(userDao.findByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        
        verify(userDao, times(1)).findByUsername(username);
    }
}