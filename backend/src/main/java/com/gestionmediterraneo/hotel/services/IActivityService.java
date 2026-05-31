package com.gestionmediterraneo.hotel.services;

import java.time.LocalDateTime;
import java.util.List;

import com.gestionmediterraneo.hotel.entities.Activity;

/**
 * Service interface for managing {@link Activity} entities.
 *
 * @author Gestión Mediterráneo
 * @see Activity
 */
public interface IActivityService {

	/** Retrieve all activities. */
	public List<Activity> findAll();

	/** Save an activity. */
	public Activity save(Activity activity);

	/** Find an activity by its ID. */
	public Activity findById(Long id);

	/** Delete an activity. */
	public Activity delete(Activity activity);

	/** Retrieve all activities sorted by the given field and direction. */
	List<Activity> findAllSorted(String sortBy, String direction);

	/** Find activities whose start date falls within the given range. */
	List<Activity> findByFechaComienzoBetween(LocalDateTime inicio, LocalDateTime fin);
}
