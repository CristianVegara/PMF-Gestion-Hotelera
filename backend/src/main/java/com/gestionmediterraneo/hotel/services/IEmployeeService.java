package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Employee;

/**
 * Service interface for managing {@link Employee} entities.
 *
 * @author Gestión Mediterráneo
 * @see Employee
 */
public interface IEmployeeService {
    /** Retrieve all employees. */
    public List<Employee> findAll();
    /** Find an employee by ID. */
    public Employee findById(Long id);
    /** Save an employee. */
    public Employee save(Employee employee);
    /** Delete an employee by ID. */
    public void delete(Long id);
    /** Find all employees without an associated user account. */
    List<Employee> findAllByUserIsNull();
}
