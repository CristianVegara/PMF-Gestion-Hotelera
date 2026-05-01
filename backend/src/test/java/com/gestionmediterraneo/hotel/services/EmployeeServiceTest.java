package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.daos.IEmployeeDAO;
import com.gestionmediterraneo.hotel.entities.Employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private IEmployeeDAO employeeDao;

    @InjectMocks
    private EmployeeServiceImp employeeService;

    @Test
    void shouldReturnAllEmployees() {
        List<Employee> employees = Arrays.asList(new Employee(), new Employee());

        when(employeeDao.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findAll();

        assertEquals(2, result.size());
        verify(employeeDao, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() {
        when(employeeDao.findAll()).thenReturn(List.of());

        List<Employee> result = employeeService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveEmployee() {
        Employee employee = new Employee();

        when(employeeDao.save(employee)).thenReturn(employee);

        Employee result = employeeService.save(employee);

        assertNotNull(result);
        verify(employeeDao, times(1)).save(employee);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Employee employee = new Employee();

        when(employeeDao.save(employee)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            employeeService.save(employee);
        });
    }

    @Test
    void shouldReturnEmployeeWhenIdExists() {
        Employee employee = new Employee();
        employee.setId(1L);

        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(employeeDao).findById(1L);
    }

    @Test
    void shouldReturnNullWhenEmployeeDoesNotExist() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        Employee result = employeeService.findById(1L);

        assertNull(result);
        verify(employeeDao).findById(1L);
    }

    @Test
    void shouldDeleteEmployee() {
        Long id = 1L;

        employeeService.delete(id);

        verify(employeeDao, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        Long id = 1L;

        doThrow(new RuntimeException("DB error")).when(employeeDao).deleteById(id);

        assertThrows(RuntimeException.class, () -> {
            employeeService.delete(id);
        });
    }
}