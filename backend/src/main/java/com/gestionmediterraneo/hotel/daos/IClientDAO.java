package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestionmediterraneo.hotel.entities.Client;

public interface IClientDAO extends JpaRepository<Client, Long> {
}