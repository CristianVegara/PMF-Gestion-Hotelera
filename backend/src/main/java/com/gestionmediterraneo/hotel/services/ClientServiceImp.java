package com.gestionmediterraneo.hotel.services;
import org.springframework.data.domain.Sort;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IClientDAO;
import com.gestionmediterraneo.hotel.entities.Client;

/**
 * Implementation of {@link IClientService} for managing {@link Client} entities.
 *
 * @author Gestión Mediterráneo
 */
@Service
public class ClientServiceImp implements IClientService {

	@Autowired
	private IClientDAO clientDao;


	/** Retrieve all clients. */
	@Override
	@Transactional(readOnly = true)
	public List<Client> findAll() {
		return (List<Client>) clientDao.findAll();
	}

	/** Retrieve all clients sorted by the given field and direction. */
	@Override
    @Transactional(readOnly = true)
    public List<Client> findAllSorted(String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return clientDao.findAll(sort);
    }

	/** Find a client by its ID. */
	@Override
	@Transactional(readOnly = true)
	public Client findById(Long id) {
		return	clientDao.findById(id).orElse(null);
	}

	/** Find a client by DNI. */
	@Override
	@Transactional(readOnly = true)
	public Client findByDni(String dni) {
		return clientDao.findByDni(dni);
	}


	/** Save a client. */
	@Override
	public Client save(Client client) {
		return clientDao.save(client);
	}

	/** Delete a client. */
	@Override
	public Client delete(Client client) {
		clientDao.delete(client);
		return client;
	}

	/** Find clients by their IDs. */
	@Override
	public List<Client> findAllByIds(List<Long> clientIds) {
		clientDao.findAllById(clientIds);
		return null;
	}



}
