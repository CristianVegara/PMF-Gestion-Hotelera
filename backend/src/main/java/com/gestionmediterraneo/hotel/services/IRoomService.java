package com.gestionmediterraneo.hotel.services;

import java.util.List;

import com.gestionmediterraneo.hotel.entities.Room;

public interface IRoomService {

    List<Room> findAll();

    Room save(Room room);

    Room findById(Long id);

    void delete(Room room);

}