package com.gestionmediterraneo.hotel.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

/**
 * Service interface for managing {@link Room} entities with dynamic pricing support.
 *
 * @author Gestión Mediterráneo
 * @see Room
 */
public interface IRoomService {
    /** Retrieve all rooms. */
    public List<Room> findAll();
    /** Find a room by ID. */
    public Room findById(Long id);
    /** Save a room. */
    public Room save(Room room);
    /** Delete a room. */
    public void delete(Room room);
    /** Get a room with its dynamically calculated price. */
    public Room getRoomWithDynamicPrice(Long id, LocalDate date);
    /** Get all rooms with dynamically calculated prices. */
    public List<Room> getAllRoomsWithDynamicPrice(LocalDate date);
    /** Get the dynamic price for a room type on a given date. */
    public Double getPriceByTypeAndDate(RoomType type, LocalDate date);
    /** Find the first room with the given status and type. */
	Optional<Room> findFirstByStatusAndType(RoomStatus status, RoomType type);
}
