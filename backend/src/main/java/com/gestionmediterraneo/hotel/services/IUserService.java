package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.User;

//Interfaz con los métodos para usar en el servicio de alumno
public interface IUserService {
	
	public List<User> findAll();

}
