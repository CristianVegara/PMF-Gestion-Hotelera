package com.gestionmediterraneo.hotel.services;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for loading user details for Spring Security authentication.
 *
 * @author Gestión Mediterráneo
 * @see UserDetails
 */
public interface IUserDetailsService {
	/**
	 * Load a user by username for authentication purposes.
	 * @param username the username to look up
	 * @return UserDetails for the given username
	 */
	public UserDetails loadUserByUsername(String username);
}
