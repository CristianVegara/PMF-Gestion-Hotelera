package com.gestionmediterraneo.hotel.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestionmediterraneo.hotel.entities.User;
import com.gestionmediterraneo.hotel.services.IUserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userService.findAll();
            return new ResponseEntity<List<User>>(users, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user}")
    public ResponseEntity<?> getEmployeeByUsername(@PathVariable String user) {
        User usuario = null;
        Map<String, Object> response = new HashMap<>();
        try {
            usuario = userService.findByUsername(user);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (usuario == null) {
            response.put("mensaje", "El usuario: ".concat(user).concat(" no existe en la base de datos"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<User>(usuario, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody User user, BindingResult result) {        
        user.setCreatedAt(LocalDate.now());
        User newUser = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            newUser = userService.save(user);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar el usuario en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido creado con éxito");
        response.put("user", newUser);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{user}")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody User user, BindingResult result, @PathVariable("user") String userName) {
    	User currentUser = userService.findByUsername(userName);
        User updatedUser = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if (currentUser == null) {
            response.put("mensaje", "Error: no se pudo editar, el usuario ".concat(userName).concat(" no existe"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentUser.setUsername(user.getUsername());
            
            if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                currentUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            
            currentUser.setRole(user.getRole());
            currentUser.setEmployee(user.getEmployee());

            updatedUser = userService.save(currentUser);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el usuario en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido actualizado con éxito");
        response.put("user", updatedUser);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{user}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String user) {
        Map<String, Object> response = new HashMap<>();
        try {
            User usuario = userService.findByUsername(user);

            if (usuario == null) {
                response.put("mensaje", "El usuario ".concat(user).concat(" no existe"));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

            userService.delete(usuario);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el usuario de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El usuario ha sido eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
}