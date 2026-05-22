package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.gestionmediterraneo.hotel.entities.Activity;
import com.gestionmediterraneo.hotel.services.IActivityService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/activities")
public class ActivityController {
	
    @Autowired
    private IActivityService activityService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public List<Activity> getActivities(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        try {
            return activityService.findAllSorted(sortBy, direction);
        } catch(Exception e) {
            System.err.println("-- ERROR EN ACTIVITIES --");
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> getActivitiesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Activity> activities = activityService.findByFechaComienzoBetween(fechaInicio, fechaFin);
            return new ResponseEntity<List<Activity>>(activities, HttpStatus.OK);
        } catch (Exception e) {
            response.put("mensaje", "Error al filtrar actividades por fecha");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
  
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Activity activity = null;
        Map<String, Object> response = new HashMap<>();
        
        try {
            activity = activityService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (activity == null) {
            response.put("mensaje", "La actividad ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<Activity>(activity, HttpStatus.OK);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Activity activity, BindingResult result) {
        Activity activityNew = null;
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
            activityNew = activityService.save(activity);
        } catch(DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La actividad ha sido creada con éxito");
        response.put("activity", activityNew);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> update(@RequestBody Activity activity, @PathVariable Long id) {
        Activity currentActivity = activityService.findById(id);
        Activity activityUpdated = null;
        Map<String, Object> response = new HashMap<>();

        if (currentActivity == null) {
            response.put("mensaje", "Error: no se pudo editar, la actividad ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentActivity.setDescripcion(activity.getDescripcion());
            currentActivity.setPrecio(activity.getPrecio());
            currentActivity.setFechaComienzo(activity.getFechaComienzo());
            currentActivity.setFechaFin(activity.getFechaFin());

            activityUpdated = activityService.save(currentActivity);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar la actividad en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La actividad ha sido actualizada con éxito");
        response.put("activity", activityUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Activity activityEliminar = activityService.findById(id);

        if (activityEliminar == null) {
            response.put("mensaje", "Error: no se pudo eliminar, la actividad ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            activityService.delete(activityEliminar);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la actividad de la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La actividad ha sido eliminada con éxito");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
    
    
}