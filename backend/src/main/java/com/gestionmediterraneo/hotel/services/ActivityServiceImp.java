package com.gestionmediterraneo.hotel.services;

import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IActivityDAO;
import com.gestionmediterraneo.hotel.entities.Activity;

@Service
public class ActivityServiceImp implements IActivityService {
	
	@Autowired
	private IActivityDAO activityDao;

	@Override
	@Transactional(readOnly = true)
	public List<Activity> findAll() {
		return (List<Activity>) activityDao.findAll();
	}

	@Override
	public Activity save(Activity activity) {
		activityDao.save(activity);
		return activity;
	}

	@Override
	public Activity findById(Long id) {
		return activityDao.findById(id).orElse(null);
	}

	@Override
	public Activity delete(Activity activity) {
		activityDao.delete(activity);
		return activity;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Activity> findAllSorted(String sortBy, String direction) {
		Sort sort = direction.equalsIgnoreCase("desc") 
				? Sort.by(sortBy).descending() 
				: Sort.by(sortBy).ascending();
		return activityDao.findAll(sort);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Activity> findByFechaComienzoBetween(LocalDateTime inicio, LocalDateTime fin) {
	    return activityDao.findByFechaComienzoBetween(inicio, fin);
	}
}