package com.gestionmediterraneo.hotel.daos;

import com.gestionmediterraneo.hotel.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceDAO extends JpaRepository<Invoice, Long> {
}