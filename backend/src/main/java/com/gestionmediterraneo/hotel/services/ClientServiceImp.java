package com.gestionmediterraneo.hotel.services;
import org.springframework.data.domain.Sort;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IClientDAO;
import com.gestionmediterraneo.hotel.entities.Client;

@Service
public class ClientServiceImp implements IClientService {
	
	@Autowired
	private IClientDAO clientDao;

	@Override
	@Transactional(readOnly = true)
	public List<Client> findAll() {
		return (List<Client>) clientDao.findAll();
	}

	@Override
	public Client save(Client client) {
		clientDao.save(client);
		return client;
	}

	@Override
	public Client findById(Long id) {
		return	clientDao.findById(id).orElse(null);
	}

	@Override
	public Client findByDni(String dni) {
		// TODO buscar por dni
		return null;
	}

	@Override
	public Client delete(Client client) {
		clientDao.delete(client);
		return client;
	}
	
	  @Override
	    @Transactional(readOnly = true)
	    public List<Client> findAllSorted(String sortBy, String direction) {
	        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
	        return clientDao.findAll(sort);
	    }

}
