package com.gestionmediterraneo.hotel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IUserDAO;
import com.gestionmediterraneo.hotel.entities.User;


/**
 * Implementation of {@link IUserService} for managing {@link User} entities.
 *
 * @author Gestión Mediterráneo
 */
@Service
public class UserServiceImp implements IUserService {

    @Autowired
    private IUserDAO userDao;

    /** Retrieve all users. */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userDao.findAll();
    }

	/** Find a user by username. */
	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	/** Save a user. */
	@Override
	public User save(User user) {
		return userDao.save(user);
	}

	/** Delete a user. */
	@Override
	public void delete(User user) {
		userDao.delete(user);
	}

	/** Find a user by associated employee ID. */
	@Override
	public User findByEmployee_Id(Long id) {
		return userDao.findByEmployee_Id(id);
	}

	/** Find a user by ID. */
	@Override
	public User findById(Long id) {
		return userDao.findById(id).orElse(null);
	}

}
