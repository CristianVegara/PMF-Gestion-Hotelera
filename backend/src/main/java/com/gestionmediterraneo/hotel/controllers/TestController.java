package com.gestionmediterraneo.hotel.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.gestionmediterraneo.hotel.services.IClientService;
import com.gestionmediterraneo.hotel.services.IUserService;

@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IClientService clientService;
	
	
    @GetMapping
    public String test() {
        return "El Backend responde la llamada";    
    }
}
