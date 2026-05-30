package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestionmediterraneo.hotel.entities.Invoice;

public interface IInvoiceDAO extends JpaRepository<Invoice, Long> {

    List<Invoice> findByFechaEmisionBetween(LocalDate start, LocalDate end);
    
    @Query("""
            SELECT i FROM Invoice i
            WHERE i.pagada = true
              AND (
                    (i.booking IS NOT NULL
                        AND i.booking.fechaEntrada <= :end
                        AND i.booking.fechaSalida  >= :start)
                    OR
                    (i.booking IS NULL
                        AND i.fechaEmision BETWEEN :start AND :end)
                  )
            """)
    List<Invoice> findPaidByBookingDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    /**
     * All invoices (paid and pending) for a client whose booking overlaps
     * the requested period.  Used for the client-expenses endpoint.
     */
    @Query("""
            SELECT i FROM Invoice i
            WHERE i.cliente.id = :clientId
              AND (
                    (i.booking IS NOT NULL
                        AND i.booking.fechaEntrada <= :end
                        AND i.booking.fechaSalida  >= :start)
                    OR
                    (i.booking IS NULL
                        AND i.fechaEmision BETWEEN :start AND :end)
                  )
            """)
    List<Invoice> findByClienteIdAndBookingDateRange(
            @Param("clientId") Long clientId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    List<Invoice> findByBooking_Id(Long bookingId);

    List<Invoice> findByFechaEmisionBetweenAndPagadaTrue(LocalDate start, LocalDate end);
}