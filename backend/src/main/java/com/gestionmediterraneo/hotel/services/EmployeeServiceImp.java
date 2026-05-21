package com.gestionmediterraneo.hotel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IEmployeeDAO;
import com.gestionmediterraneo.hotel.entities.Employee;

@Service
public class EmployeeServiceImp implements IEmployeeService {

    @Autowired
    private IEmployeeDAO employeeDao;

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return (List<Employee>) employeeDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Employee save(Employee employee) {
        return employeeDao.save(employee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeDao.deleteById(id);
    }

	@Override
	public List<Employee> findAllByUserIsNull() {
		return employeeDao.findAllByUserIsNull();
	}
}