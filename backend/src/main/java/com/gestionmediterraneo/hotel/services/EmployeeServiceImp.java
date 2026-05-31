package com.gestionmediterraneo.hotel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IEmployeeDAO;
import com.gestionmediterraneo.hotel.entities.Employee;

/**
 * Implementation of {@link IEmployeeService} for managing {@link Employee} entities.
 *
 * @author Gestión Mediterráneo
 */
@Service
public class EmployeeServiceImp implements IEmployeeService {

    @Autowired
    private IEmployeeDAO employeeDao;

    /** Retrieve all employees. */
    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return (List<Employee>) employeeDao.findAll();
    }

    /** Find an employee by ID. */
    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeDao.findById(id).orElse(null);
    }

    /** Save an employee. */
    @Override
    @Transactional
    public Employee save(Employee employee) {
        return employeeDao.save(employee);
    }

    /** Delete an employee by ID. */
    @Override
    @Transactional
    public void delete(Long id) {
        employeeDao.deleteById(id);
    }

	/** Find all employees without an associated user account. */
	@Override
	public List<Employee> findAllByUserIsNull() {
		return employeeDao.findAllByUserIsNull();
	}
}
