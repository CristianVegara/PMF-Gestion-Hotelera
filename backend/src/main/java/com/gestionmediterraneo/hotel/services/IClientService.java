package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Client;

public interface IClientService {
	public List<Client> findAll();
	
	public Client save(Client client);
	
	public Client findById(Long id);
	
	public List<Client> findAllByIds(List<Long> clientIds);
	
	public Client findByDni(String dni);
	
	public Client delete(Client client);
	
	List<Client> findAllSorted(String sortBy, String direction);	

}
