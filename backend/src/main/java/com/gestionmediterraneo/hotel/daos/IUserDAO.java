package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.User;

public interface IUserDAO extends CrudRepository<User, Long>{

}
