package com.gestionmediterraneo.hotel.services;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.entities.Invoice;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link Invoice} entities.
 *
 * @author Gestión Mediterráneo
 */
@Service
public class InvoiceService {

    /** Invoice DAO. */
    private final IInvoiceDAO invoiceDao;

    /**
     * Constructor for InvoiceService.
     * @param invoiceDao invoice data access object
     */
    public InvoiceService(IInvoiceDAO invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    /** Retrieve all invoices. */
    public List<Invoice> findAll() {
        return invoiceDao.findAll();
    }

    /** Retrieve all invoices sorted by the given field and direction. */
    public List<Invoice> findAllSorted(String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return invoiceDao.findAll(sort);
    }

    /** Find an invoice by ID. */
    public Optional<Invoice> findById(Long id) {
        return invoiceDao.findById(id);
    }

    /** Find invoices issued within the given date range. */
    public List<Invoice> findByDateRange(LocalDate start, LocalDate end) {
        return invoiceDao.findByFechaEmisionBetween(start, end);
    }

    /** Find paid invoices whose booking overlaps the given date range. */
    public List<Invoice> findPaidByBookingDateRange(LocalDate start, LocalDate end) {
        return invoiceDao.findPaidByBookingDateRange(start, end);
    }
    /** Find all invoices for a client whose booking overlaps the given date range. */
    public List<Invoice> findByClientBookingDateRange(Long clientId, LocalDate start, LocalDate end) {
        return invoiceDao.findByClienteIdAndBookingDateRange(clientId, start, end);
    }

    /** Save an invoice. */
    public Invoice save(Invoice invoice) {
        return invoiceDao.save(invoice);
    }

    /** Delete an invoice by ID. */
    public void delete(Long id) {
        invoiceDao.deleteById(id);
    }
}
