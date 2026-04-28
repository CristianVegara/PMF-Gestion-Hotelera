package com.gestionmediterraneo.hotel.services;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Discount;
import com.gestionmediterraneo.hotel.entities.Invoice;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<Invoice> findAllSorted(String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return invoiceDao.findAll(sort);
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceDao.findById(id);
    }

    public Invoice save(Invoice invoice) {

        BigDecimal total = invoice.getTotal();

        Client cliente = invoice.getCliente();

        if (cliente != null &&
            cliente.getDiscounts() != null &&
            !cliente.getDiscounts().isEmpty()) {

            double porcentajeTotal = 0;

            for (Discount d : cliente.getDiscounts()) {
                porcentajeTotal += d.getPorcentaje();
            }

            BigDecimal descuento = total.multiply(
                BigDecimal.valueOf(porcentajeTotal / 100)
            );

            BigDecimal totalFinal = total.subtract(descuento);

            invoice.setTotal(totalFinal);
        }

        return invoiceDao.save(invoice);
    }

    public void delete(Long id) {
        invoiceDao.deleteById(id);
    }
}