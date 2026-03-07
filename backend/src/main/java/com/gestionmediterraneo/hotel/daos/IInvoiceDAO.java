package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestionmediterraneo.hotel.entities.Invoice;

public interface IInvoiceDAO extends JpaRepository<Invoice, Long> {
}