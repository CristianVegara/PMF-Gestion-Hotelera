package com.gestionmediterraneo.hotel.daos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

/**
 * Data Access Object for {@link Room} entities.
 *
 * @author Gestión Mediterráneo
 */
@Repository
public interface IRoomDAO extends JpaRepository<Room, Long> {
	/** Count rooms by status. */
	long countByStatus(RoomStatus status);
	/** Count rooms by type and status. */
	long countByTypeAndStatus(RoomType type, RoomStatus status);
	/** Find the first room of the given type. */
	Optional<Room> findFirstByType(RoomType type);
	/** Find the first room with the given status and type. */
	Optional<Room> findFirstByStatusAndType(RoomStatus status, RoomType type);
}
