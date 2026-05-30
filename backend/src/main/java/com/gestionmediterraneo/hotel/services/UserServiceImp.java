package com.gestionmediterraneo.hotel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IUserDAO;
import com.gestionmediterraneo.hotel.entities.User;


@Service
public class UserServiceImp implements IUserService {

    @Autowired
    private IUserDAO userDao;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userDao.findAll();
    }

	@Override
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	public User save(User user) {
		return userDao.save(user);
	}

	@Override
	public void delete(User user) {
		userDao.delete(user);
	}

	@Override
	public User findByEmployee_Id(Long id) {
		return userDao.findByEmployee_Id(id);
	}

	@Override
	public User findById(Long id) {
		return userDao.findById(id).orElse(null);
	}

}