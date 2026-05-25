package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getClients(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        List<Client> clients = null;
        Map<String, Object> response = new HashMap<>();

        try {
            clients = clientService.findAllSorted(sortBy, direction);
        } catch (Exception e) {
            System.err.println("-- ERROR EN CLIENTES --");
            e.printStackTrace();
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Client>>(clients, HttpStatus.OK);
    }
    
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
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

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> history(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Client client = clientService.findById(id);

        if (client == null) {
            response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        response.put("client", client);
        response.put("bookings", client.getBookings());
        response.put("activities", client.getActivities());
        response.put("invoices", client.getInvoices());
        response.put("payments", client.getPayments());
        response.put("refunds", client.getRefunds());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }    
    
    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
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
        
        if (client.getCorreo() == null || !client.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            response.put("mensaje", "El email no tiene un formato válido (xxx@x.x)");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
    @PreAuthorize("hasAnyRole('RECEPCIONISTA')")
    public ResponseEntity<?> update(@Valid @RequestBody Client client, BindingResult result, @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
					                    .stream()
					                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					                    .collect(Collectors.toList());

            response.put("Errores en los campos", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        if (client.getCorreo() == null || !client.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            response.put("mensaje", "El email no tiene un formato válido (xxx@x.x)");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Client currentClient = clientService.findById(id);

        if (currentClient == null) {
            response.put("mensaje", "Cliente no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentClient.setNombre(client.getNombre());
            currentClient.setDni(client.getDni());
            currentClient.setTelefono(client.getTelefono());
            currentClient.setCorreo(client.getCorreo());

            Client updated = clientService.save(currentClient);

            response.put("mensaje", "Actualizado con éxito");
            response.put("client", updated);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error en la base de datos");
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR')")
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
