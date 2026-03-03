package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Client;

public interface IClientDAO extends CrudRepository<Client, Long> {

}
