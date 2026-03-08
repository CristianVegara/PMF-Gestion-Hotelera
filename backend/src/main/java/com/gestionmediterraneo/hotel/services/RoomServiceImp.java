package com.gestionmediterraneo.hotel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Room;

@Service
public class RoomServiceImp implements IRoomService {

	@Autowired
    private IRoomDAO roomDAO;

    @Override
    public List<Room> findAll() {
        return roomDAO.findAll();
    }

    @Override
    public Room findById(Long id) {
        return roomDAO.findById(id).orElse(null);
    }

    @Override
    public Room save(Room room) {
        return roomDAO.save(room);
    }

    @Override
    public void delete(Room room) {
        roomDAO.delete(room);
    }
}