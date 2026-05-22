package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gestionmediterraneo.hotel.daos.IInvoiceDAO;
import com.gestionmediterraneo.hotel.daos.IPaymentDAO;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.entities.Payment;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:3000"})
public class PaymentController {

    @Autowired
    private IPaymentDAO paymentDao;

    @Autowired
    private IInvoiceDAO invoiceDao;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> createPayment(@RequestBody Payment payment) {
        Map<String, Object> response = new HashMap<>();
        try {
            Invoice invoice = invoiceDao.findById(payment.getInvoice().getId()).orElse(null);
            if (invoice == null) {
                response.put("mensaje", "Factura no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            payment.setInvoice(invoice);
            payment.setClient(invoice.getCliente());
            Payment saved = paymentDao.save(payment);
            invoice.setPagada(true);
            invoiceDao.save(invoice);
            response.put("mensaje", "Pago registrado con éxito");
            response.put("data", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar pago");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
