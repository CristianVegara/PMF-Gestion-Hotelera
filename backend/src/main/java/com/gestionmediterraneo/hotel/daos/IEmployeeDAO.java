package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Employee;

public interface IEmployeeDAO extends CrudRepository<Employee, Long>{

}
