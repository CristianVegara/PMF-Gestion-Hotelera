package com.gestionmediterraneo.hotel.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Room;

@Service
public class RoomServiceImp implements IRoomService {

    @Autowired
    private IRoomDAO roomDao;

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return (List<Room>) roomDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Room findById(Long id) {
        return roomDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Room save(Room room) {
        return roomDao.save(room);
    }

    @Override
    @Transactional
    public void delete(Room room) {
        roomDao.delete(room);
    }
}