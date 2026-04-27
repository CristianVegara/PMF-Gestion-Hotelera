package com.gestionmediterraneo.hotel.daos; 


import com.gestionmediterraneo.hotel.entities.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookingDAO extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.habitacion.id = :roomId")
    List<Booking> buscarPorHabitacion(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.habitacion.id = :roomId " +
           "AND :fEntrada < b.fechaSalida AND :fSalida > b.fechaEntrada")
    boolean estaOcupada(@Param("roomId") Long roomId, 
                        @Param("fEntrada") java.time.LocalDate fEntrada, 
                        @Param("fSalida") java.time.LocalDate fSalida);
	
}
