package com.gestionmediterraneo.hotel.daos; 


import com.gestionmediterraneo.hotel.entities.Booking;
import com.gestionmediterraneo.hotel.enums.CheckInStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface IBookingDAO extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.habitacion.id = :roomId")
    List<Booking> buscarPorHabitacion(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.habitacion.id = :roomId " +
           "AND :fEntrada < b.fechaSalida AND :fSalida > b.fechaEntrada")
    boolean estaOcupada(@Param("roomId") Long roomId, 
                        @Param("fEntrada") java.time.LocalDate fEntrada, 
                        @Param("fSalida") java.time.LocalDate fSalida);
    
   @Query("SELECT COUNT(b) FROM Booking b WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND b.estado <> com.gestionmediterraneo.hotel.enums.BookingStatus.CANCELADA")
    long countOccupiedByDate(@Param("fecha") java.time.LocalDate fecha);

    @Query("SELECT COUNT(b) FROM Booking b JOIN b.habitacion r WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND r.type = :type AND b.estado <> com.gestionmediterraneo.hotel.enums.BookingStatus.CANCELADA")
    long countOccupiedByDateAndType(@Param("fecha") java.time.LocalDate fecha, @Param("type") RoomType type);
    
    //@Query("SELECT b FROM Booking b WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND b.estado != 'CANCELADA'")
    //List<Booking> findBookingsByDate(@Param("fecha") LocalDate fecha);
    
    long countByCliente_Id(Long clientId);

    long countByCliente_IdAndFechaEntradaBetween(Long clientId, LocalDate startDate, LocalDate endDate);	
	
    @Query("SELECT b FROM Booking b WHERE :fecha BETWEEN b.fechaEntrada AND b.fechaSalida AND b.estado <> com.gestionmediterraneo.hotel.enums.BookingStatus.CANCELADA")
    List<Booking> findBookingsByDate(@Param("fecha") java.time.LocalDate fecha);
    
    List<Booking> findByCheckInStatus(CheckInStatus checkInStatus);	
}