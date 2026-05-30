package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Shift;

/**
 * Data Access Object for {@link Shift} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IShiftDAO extends CrudRepository<Shift, Long> {
	/**
	 * Find shifts within a date range.
	 * @param inicio start date (inclusive)
	 * @param fin   end date (inclusive)
	 */
	List<Shift> findByFechaBetween(LocalDate inicio, LocalDate fin);

	/**
	 * Find all shifts with their employee and schedule eagerly fetched.
	 */
	@Query("SELECT s FROM Shift s JOIN FETCH s.employee JOIN FETCH s.schedule")
    List<Shift> findAllWithDetails();
}
