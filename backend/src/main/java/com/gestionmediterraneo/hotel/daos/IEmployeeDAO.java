package com.gestionmediterraneo.hotel.daos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Employee;

/**
 * Data Access Object for {@link Employee} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IEmployeeDAO extends CrudRepository<Employee, Long>{
	/** Find all employees that do not have an associated user account. */
	List<Employee> findAllByUserIsNull();
}
