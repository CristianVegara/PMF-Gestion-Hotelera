package com.gestionmediterraneo.hotel.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.entities.Employee;
import com.gestionmediterraneo.hotel.daos.IUserDAO;
import com.gestionmediterraneo.hotel.daos.IEmployeeDAO;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private IUserDAO userDao;

    @Autowired
    private IEmployeeDAO employeeDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
    	if (userDao.findByUsername("admin") == null) {
            
    		User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPasswordHash(passwordEncoder.encode("admin"));
            adminUser.setRole("ADMIN");
            
            Employee adminEmployee = new Employee();
            adminEmployee.setNombre("Admin");
            adminEmployee.setApellido("Hotel");
            adminEmployee.setCargo("ADMIN");
            
            adminEmployee.setUser(adminUser);    
            adminUser.setEmployee(adminEmployee);
            
            employeeDao.save(adminEmployee);
        }
    }
}