package com.gestionmediterraneo.hotel.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Charge;

/**
 * Data Access Object for {@link Charge} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IChargeDAO extends JpaRepository<Charge, Long> {
    /**
     * Find all charges associated with a specific booking.
     * @param bookingId the booking identifier
     * @return charges for the given booking
     */
    List<Charge> findByBookingId(Long bookingId);
}
