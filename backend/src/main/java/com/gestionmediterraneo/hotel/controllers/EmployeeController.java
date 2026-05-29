package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gestionmediterraneo.hotel.entities.Employee;
import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.services.IEmployeeService;
import com.gestionmediterraneo.hotel.services.IUserService;


@CrossOrigin(origins = {"http://localhost:3000"}) 
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;
    
    @Autowired
    private IUserService userService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public List<Employee> getEmployees() {
        try {
            return employeeService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public Employee getEmployee( @PathVariable Long id) {
    	try {
            return employeeService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/no-user")
    @PreAuthorize("hasAnyRole('USER')")
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
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public Employee createEmployee(@RequestBody Employee employee) {
    	try {
    		if (employee.getUser() != null && employee.getUser().getId() != null) {
    	        User user = userService.findById(employee.getUser().getId());
    	        employee.setUser(user);
    	    }
    	    return employeeService.save(employee);     	
            
    	}
    	catch(Exception e) {
    		return null;
    	}

    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        Employee employee = employeeService.findById(id);
        if(employee != null) {
            employee.setNombre(employeeDetails.getNombre());
            employee.setApellido(employeeDetails.getApellido());
            employee.setCargo(employeeDetails.getCargo());
            if (employeeDetails.getUser() != null && employeeDetails.getUser().getId() != null) {
                User user = userService.findById(employeeDetails.getUser().getId());
                employee.setUser(user);
            }             
            
            return employeeService.save(employee);
        }
        return null; 
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')") 
    public ResponseEntity<Employee> deleteEmployee(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }        
        
        if (employee.getUser() != null) {
            employee.setUser(null);
            employeeService.save(employee);
        }
        
        employeeService.delete(id);
        return ResponseEntity.ok(employee);
    }
}