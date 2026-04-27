package com.gestionmediterraneo.hotel.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionmediterraneo.hotel.entities.Room;

@Repository
public interface IRoomDAO extends JpaRepository<Room, Long> {
}  