package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.services.IClientService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/clients")
public class ClientController {
	
    @Autowired
    private IClientService clientService;

    @GetMapping
    public List<Client> getClients() {
        try {
            return clientService.findAll();
        } catch(Exception e) {
            System.err.println("-- ERROR EN CLIENTES --");
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Client client = null;
        Map<String, Object> response = new HashMap<>();
        
        try {
            client = clientService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (client == null) {
            response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<Client>(client, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Client client, BindingResult result) {
        Client clientNew = null;
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
					                    .stream()
					                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            clientNew = clientService.save(client);
        } catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido creado con éxito");
        response.put("client", clientNew);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Client client, @PathVariable Long id) {
        Client currentClient = clientService.findById(id);
        Client clientUpdated = null;
        Map<String, Object> response = new HashMap<>();

        if (currentClient == null) {
            response.put("mensaje", "Error: no se pudo editar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentClient.setNombre(client.getNombre());
            currentClient.setDni(client.getDni());
            currentClient.setTelefono(client.getTelefono());
            currentClient.setCorreo(client.getCorreo());

            clientUpdated = clientService.save(currentClient);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el cliente en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido actualizado con éxito");
        response.put("client", clientUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Client clientEliminar = clientService.findById(id);

        if (clientEliminar == null) {
            response.put("mensaje", "Error: no se pudo eliminar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            clientService.delete(clientEliminar);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el cliente de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido eliminado con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    
}
