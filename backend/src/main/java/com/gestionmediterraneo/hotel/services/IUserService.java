package com.gestionmediterraneo.hotel.services;

import java.util.List;
import com.gestionmediterraneo.hotel.entities.User;

public interface IUserService {
	
	public List<User> findAll();
	public User findByUsername(String username);
	public User findById(Long id);
	public User save(User user);
	public void delete(User user);
	public User findByEmployee_Id(Long id);

}
