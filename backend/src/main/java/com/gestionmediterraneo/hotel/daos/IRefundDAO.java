package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Refund;

/**
 * Data Access Object for {@link Refund} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IRefundDAO extends JpaRepository<Refund, Long> {
}
