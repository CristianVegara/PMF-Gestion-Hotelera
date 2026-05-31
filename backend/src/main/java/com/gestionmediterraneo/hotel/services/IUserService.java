package com.gestionmediterraneo.hotel.services;

import java.util.List;
import com.gestionmediterraneo.hotel.entities.User;

/**
 * Service interface for managing {@link User} entities.
 *
 * @author Gestión Mediterráneo
 * @see User
 */
public interface IUserService {

	/** Retrieve all users. */
	public List<User> findAll();
	/** Find a user by username. */
	public User findByUsername(String username);
	/** Find a user by ID. */
	public User findById(Long id);
	/** Save a user. */
	public User save(User user);
	/** Delete a user. */
	public void delete(User user);
	/** Find a user by associated employee ID. */
	public User findByEmployee_Id(Long id);

}
