package com.gestionmediterraneo.hotel.services;

import java.time.LocalDateTime;
import java.util.List;

import com.gestionmediterraneo.hotel.entities.Activity;

public interface IActivityService {
	
	public List<Activity> findAll();
	
	public Activity save(Activity activity);
	
	public Activity findById(Long id);
	
	public Activity delete(Activity activity);
	
	List<Activity> findAllSorted(String sortBy, String direction);
	
	List<Activity> findByFechaComienzoBetween(LocalDateTime inicio, LocalDateTime fin);
}