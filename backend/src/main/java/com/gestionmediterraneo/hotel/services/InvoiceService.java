package com.gestionmediterraneo.hotel.services;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.entities.Invoice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final IInvoiceDAO invoiceDao;

    public InvoiceService(IInvoiceDAO invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    public List<Invoice> findAll() {
        return invoiceDao.findAll();
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceDao.findById(id);
    }

    public Invoice save(Invoice invoice) {
        return invoiceDao.save(invoice);
    }

    public void delete(Long id) {
        invoiceDao.deleteById(id);
    }
}