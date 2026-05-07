package com.gestionmediterraneo.hotel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomType;
import com.gestionmediterraneo.hotel.services.IRoomService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @GetMapping
    public List<Room> getRooms() {
        return roomService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Room room = null;
        Map<String, Object> response = new HashMap<>();

        try {
            room = roomService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (room == null) {
            response.put("mensaje", "La habitación ID: " + id + " no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Room room, BindingResult result) {
        Room roomNew = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            return validarCampos(result);
        }

        try {
            roomNew = roomService.save(room);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La habitación ha sido creada con éxito");
        response.put("room", roomNew);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/dynamic")
    public List<Room> getAllDynamic() {
        return roomService.getAllRoomsWithDynamicPrice(LocalDate.now());
    }

    @GetMapping("/dynamic/{id}")
    public ResponseEntity<?> showDynamic(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Room room = roomService.getRoomWithDynamicPrice(id, LocalDate.now());
            if (room == null) {
                response.put("mensaje", "La habitación ID: " + id + " no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dynamic/chart/type/{type}")
    public ResponseEntity<?> getPriceChartByType(@PathVariable RoomType type) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> labels = new ArrayList<>();
            List<Double> prices = new ArrayList<>();
            
            LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

            for (int i = 11; i >= 0; i--) {
                LocalDate targetDate = currentMonth.minusMonths(i);
                Double price = roomService.getPriceByTypeAndDate(type, targetDate);
                
                labels.add(targetDate.getMonth().name() + " " + targetDate.getYear());
                prices.add(price);
            }

            Map<String, Object> chartData = new LinkedHashMap<>();
            chartData.put("type", type);
            chartData.put("labels", labels);
            chartData.put("datasets", prices);

            return new ResponseEntity<>(chartData, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al generar la gráfica por tipo");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/dynamic/chart/all-types")
    public ResponseEntity<?> getCombinedPriceChart() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<String> labels = new ArrayList<>();
            Map<RoomType, List<Double>> datasets = new LinkedHashMap<>();
            
            for (RoomType type : RoomType.values()) {
                datasets.put(type, new ArrayList<>());
            }

            LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

            for (int i = 11; i >= 0; i--) {
                LocalDate targetDate = currentMonth.minusMonths(i);
                labels.add(targetDate.getMonth().name() + " " + targetDate.getYear());

                for (RoomType type : RoomType.values()) {
                    Double price = roomService.getPriceByTypeAndDate(type, targetDate);
                    datasets.get(type).add(price);
                }
            }

            response.put("labels", labels);
            response.put("datasets", datasets);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al procesar el histórico combinado: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Room room, BindingResult result, @PathVariable Long id) {
        Room currentRoom = roomService.findById(id);
        Room roomUpdated = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            return validarCampos(result);
        }

        if (currentRoom == null) {
            response.put("mensaje", "Error: no se pudo editar, el ID: " + id + " no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            currentRoom.setNumber(room.getNumber());
            currentRoom.setType(room.getType());
            currentRoom.setPrice(room.getPrice());
            currentRoom.setStatus(room.getStatus());
            roomUpdated = roomService.save(currentRoom);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La habitación ha sido actualizada con éxito");
        response.put("room", roomUpdated);
        return new ResponseEntity<>(response, HttpStatus.OK); // Cambiado a OK (200)
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Room roomEliminar = roomService.findById(id);

        if (roomEliminar == null) {
            response.put("mensaje", "Error: ID no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            roomService.delete(roomEliminar);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "La habitación ha sido eliminada con éxito");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<?> validarCampos(BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = result.getFieldErrors()
                .stream()
                .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                .collect(Collectors.toList());
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}