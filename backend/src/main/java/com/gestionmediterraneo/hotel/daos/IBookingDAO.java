package com.gestionmediterraneo.hotel.daos;


import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.enums.BookingStatus;
import com.gestionmediterraneo.hotel.enums.CheckInStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object for {@link Booking} entities.
 *
 * @author Gestión Mediterráneo
 */
@Repository
public interface IBookingDAO extends JpaRepository<Booking, Long> {

    /**
     * Find all bookings for a specific room.
     * @param roomId the room identifier
     */
    @Query("SELECT b FROM Booking b WHERE b.habitacion.id = :roomId")
    List<Booking> buscarPorHabitacion(@Param("roomId") Long roomId);

    /**
     * Check whether a room is occupied during the given date range.
     * @param roomId   the room identifier
     * @param fEntrada check-in date
     * @param fSalida  check-out date
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.habitacion.id = :roomId " +
           "AND :fEntrada < b.fechaSalida AND :fSalida > b.fechaEntrada")
    boolean estaOcupada(@Param("roomId") Long roomId,
                        @Param("fEntrada") java.time.LocalDate fEntrada,
                        @Param("fSalida") java.time.LocalDate fSalida);

    /**
     * Count all non-cancelled bookings occupied on the given date.
     * @param fecha the date to check
     */
   @Query("SELECT COUNT(b) FROM Booking b WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND b.estado <> com.gestionmediterraneo.hotel.enums.BookingStatus.CANCELADA")
    long countOccupiedByDate(@Param("fecha") java.time.LocalDate fecha);

    /**
     * Count bookings of a specific room type occupied on the given date.
     * @param fecha the date to check
     * @param type  the room type
     */
    @Query("SELECT COUNT(b) FROM Booking b JOIN b.habitacion r WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND r.type = :type AND b.estado <> com.gestionmediterraneo.hotel.enums.BookingStatus.CANCELADA")
    long countOccupiedByDateAndType(@Param("fecha") java.time.LocalDate fecha, @Param("type") RoomType type);

    /**
     * Find bookings active on the given date with a specific status.
     * @param fecha  the date to check
     * @param estado the status to exclude
     */
    @Query("SELECT b FROM Booking b WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND b.estado != :estado")
    List<Booking> findBookingsByDate(@Param("fecha") LocalDate fecha, @Param("estado")BookingStatus estado);

    /** Count bookings by client ID. */
    long countByCliente_Id(Long clientId);

    /** Count bookings by client ID within a date range. */
    long countByCliente_IdAndFechaEntradaBetween(Long clientId, LocalDate startDate, LocalDate endDate);

    /** Find bookings by check-in status. */
    List<Booking> findByCheckInStatus(CheckInStatus checkInStatus);

    /** Find bookings by room ID. */
    List<Booking> findByHabitacionId(Long id);
}
