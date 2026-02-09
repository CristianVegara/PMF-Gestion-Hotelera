package com.gestionmediterraneo.hotel.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.services.IUserService;

@RestController
@RequestMapping("/api")
public class TestController {
	
	@Autowired
	private IUserService userService;
	
	
    @GetMapping("/test")
    public String test() {
        return "El Backend responde la llamada";
    }
    
    
    //Hace una llamada de los usuarios, si hay algún problema saca el error
    @GetMapping("/test/users")
    public List<User> testUsers() {
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
