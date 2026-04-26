package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Employee;

public interface IEmployeeService {
    public List<Employee> findAll();
    public Employee findById(Long id);
    public Employee save(Employee employee);
    public void delete(Long id);
}