package com.gestionmediterraneo.hotel.daos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

@Repository
public interface IRoomDAO extends JpaRepository<Room, Long> {
	long countByStatus(RoomStatus status);
	long countByTypeAndStatus(RoomType type, RoomStatus status);
	Optional<Room> findFirstByStatusAndType(RoomStatus status, RoomType type);
}  