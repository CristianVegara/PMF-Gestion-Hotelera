package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Client;

/**
 * Service interface for managing {@link Client} entities.
 *
 * @author Gestión Mediterráneo
 * @see Client
 */
public interface IClientService {
	/** Retrieve all clients. */
	public List<Client> findAll();

	/** Save a client. */
	public Client save(Client client);

	/** Find a client by its ID. */
	public Client findById(Long id);

	/** Find clients by their IDs. */
	public List<Client> findAllByIds(List<Long> clientIds);

	/** Find a client by DNI. */
	public Client findByDni(String dni);

	/** Delete a client. */
	public Client delete(Client client);

	/** Retrieve all clients sorted by the given field and direction. */
	List<Client> findAllSorted(String sortBy, String direction);

}
