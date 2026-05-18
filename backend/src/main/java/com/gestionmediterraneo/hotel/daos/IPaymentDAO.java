package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Payment;

public interface IPaymentDAO extends JpaRepository<Payment, Long> {
}
