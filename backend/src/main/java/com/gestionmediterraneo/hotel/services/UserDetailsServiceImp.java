package com.gestionmediterraneo.hotel.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.daos.IUserDAO;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private IUserDAO userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el username: " + username);
        }

        return UserDetailsImp.build(user);
    }
}