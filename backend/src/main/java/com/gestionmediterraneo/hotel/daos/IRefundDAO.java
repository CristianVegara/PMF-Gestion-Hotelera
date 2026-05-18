package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Refund;

public interface IRefundDAO extends JpaRepository<Refund, Long> {
}
