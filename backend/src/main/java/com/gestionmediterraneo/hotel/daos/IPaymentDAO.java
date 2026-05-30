package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Payment;

/**
 * Data Access Object for {@link Payment} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IPaymentDAO extends JpaRepository<Payment, Long> {
}
