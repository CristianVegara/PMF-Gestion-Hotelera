package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestionmediterraneo.hotel.entities.Client;

/**
 * Data Access Object for {@link Client} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IClientDAO extends JpaRepository<Client, Long> {
	/** Find client by DNI (national ID number). */
	Client findByDni(String dni);
}
