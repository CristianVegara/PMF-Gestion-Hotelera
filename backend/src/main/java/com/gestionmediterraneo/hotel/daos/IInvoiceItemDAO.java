package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.InvoiceItem;

/**
 * Data Access Object for {@link InvoiceItem} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IInvoiceItemDAO extends JpaRepository<InvoiceItem, Long> {
    /**
     * Find invoice items whose parent invoice was issued within the given date range.
     * @param start start date (inclusive)
     * @param end   end date (inclusive)
     * @return matching invoice items
     */
    List<InvoiceItem> findByInvoice_FechaEmisionBetween(LocalDate start, LocalDate end);
}
