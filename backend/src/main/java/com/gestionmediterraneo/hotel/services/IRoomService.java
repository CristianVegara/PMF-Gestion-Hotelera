package com.gestionmediterraneo.hotel.services;

import java.util.List;
import com.gestionmediterraneo.hotel.entities.Room;

public interface IRoomService {
    public List<Room> findAll();
    public Room findById(Long id);
    public Room save(Room room);
    public void delete(Room room);
}