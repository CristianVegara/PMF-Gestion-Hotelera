package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.InvoiceItem;

public interface IInvoiceItemDAO extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoice_FechaEmisionBetween(LocalDate start, LocalDate end);
}
