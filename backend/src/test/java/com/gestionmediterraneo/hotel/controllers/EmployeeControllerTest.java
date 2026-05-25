package com.gestionmediterraneo.hotel.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmediterraneo.hotel.entities.Employee;
import com.gestionmediterraneo.hotel.security.JwtUtils;
import com.gestionmediterraneo.hotel.services.IEmployeeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmployeeController.class, properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnEmployees() throws Exception {
        when(employeeService.findAll()).thenReturn(Arrays.asList(new Employee(), new Employee()));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowExceptionWhenGetEmployeesFails() throws Exception {
        when(employeeService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnEmployeesWithoutUser() throws Exception {
        when(employeeService.findAllByUserIsNull()).thenReturn(Arrays.asList(new Employee(), new Employee()));

        mockMvc.perform(get("/api/employees/no-user"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnInternalServerErrorWhenGetEmployeesWithoutUserThrowsDataAccessException() throws Exception {
        when(employeeService.findAllByUserIsNull()).thenThrow(new TransientDataAccessResourceException("Error", new RuntimeException("Cause")));

        mockMvc.perform(get("/api/employees/no-user"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setNombre("Juan");
        employee.setApellido("Pérez");
        employee.setCargo("Recepcionista");

        when(employeeService.save(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk()); 
    }

    @Test
    void shouldUpdateEmployeeWhenExists() throws Exception {
        Long id = 1L;
        Employee existingEmployee = new Employee();
        existingEmployee.setId(id);

        Employee details = new Employee();
        details.setNombre("Juan Actualizado");
        details.setApellido("Pérez");
        details.setCargo("Gerente");

        when(employeeService.findById(id)).thenReturn(existingEmployee);
        when(employeeService.save(any(Employee.class))).thenReturn(details);

        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWithNullBodyWhenUpdatingNonExistingEmployee() throws Exception {
        when(employeeService.findById(anyLong())).thenReturn(null);

        Employee details = new Employee();
        details.setNombre("Test");

        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isOk());
    }
}