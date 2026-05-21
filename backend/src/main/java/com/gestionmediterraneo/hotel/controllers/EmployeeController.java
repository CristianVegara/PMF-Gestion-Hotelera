package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gestionmediterraneo.hotel.entities.Employee;
import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.services.IEmployeeService;

@CrossOrigin(origins = {"http://localhost:3000"}) 
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    @GetMapping
    public List<Employee> getEmployees() {
        try {
            return employeeService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/no-user")
    public ResponseEntity<?> getEmployeesWithoutUser() {

        Map<String, Object> response = new HashMap<>();

        try {
            List<Employee> users = employeeService.findAllByUserIsNull();
            return new ResponseEntity<List<Employee>>(users, HttpStatus.OK);

        } catch (DataAccessException e) {

            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ")
                            .concat(e.getMostSpecificCause().getMessage()));

            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }
    
    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee employee = employeeService.findById(id);
        if(employee != null) {
            employee.setNombre(employeeDetails.getNombre());
            employee.setApellido(employeeDetails.getApellido());
            employee.setCargo(employeeDetails.getCargo());
            employee.setUser(employeeDetails.getUser());
            return employeeService.save(employee);
        }
        return null; 
    }
}