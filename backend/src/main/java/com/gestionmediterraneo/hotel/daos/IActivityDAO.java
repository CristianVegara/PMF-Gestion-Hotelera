package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Activity;

/**
 * Data Access Object for {@link Activity} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IActivityDAO extends JpaRepository<Activity, Long> {
	/**
	 * Find activities whose start date falls within the given range.
	 * @param inicio start datetime (inclusive)
	 * @param fin   end datetime (inclusive)
	 * @return activities within the date range
	 */
	List<Activity> findByFechaComienzoBetween(LocalDateTime inicio, LocalDateTime fin);
}
