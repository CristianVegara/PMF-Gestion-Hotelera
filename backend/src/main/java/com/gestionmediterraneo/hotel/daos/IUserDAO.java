package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.User;

/**
 * Data Access Object for {@link User} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IUserDAO extends CrudRepository<User, Long>{
	/** Find user by username. */
	public User findByUsername(String username);
	/** Find user by associated employee ID. */
	public User findByEmployee_Id(Long id);
}
