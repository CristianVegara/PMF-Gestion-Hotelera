package com.gestionmediterraneo.hotel.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.services.IUserService;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    public List<User> getUsers() {
    	try {
            List<User> users = userService.findAll();
            System.out.println("Usuarios encontrados: " + users.size());
            return users;
        } catch (Exception e) {
            System.err.println("--- ERROR DETECTADO ---");
            e.printStackTrace();
            throw e;
        }
    }
}