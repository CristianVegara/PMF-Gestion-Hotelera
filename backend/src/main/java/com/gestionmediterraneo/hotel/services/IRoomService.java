package com.gestionmediterraneo.hotel.services;

import java.time.LocalDate;
import java.util.List;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomType;

public interface IRoomService {
    public List<Room> findAll();
    public Room findById(Long id);
    public Room save(Room room);
    public void delete(Room room);
    public Room getRoomWithDynamicPrice(Long id, LocalDate date);
    public List<Room> getAllRoomsWithDynamicPrice(LocalDate date);
    public Double getPriceByTypeAndDate(RoomType type, LocalDate date);
}