package com.gestionmediterraneo.hotel.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Charge;

public interface IChargeDAO extends JpaRepository<Charge, Long> {
    List<Charge> findByBookingId(Long bookingId);
}
