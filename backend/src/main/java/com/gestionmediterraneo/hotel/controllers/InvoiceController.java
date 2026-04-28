package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Discount;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.services.InvoiceService;
import com.gestionmediterraneo.hotel.daos.IClientDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private IClientDAO clientDao;

    @GetMapping
    public List<Invoice> getInvoices(@RequestParam(defaultValue = "id") String sortBy,
                                     @RequestParam(defaultValue = "asc") String direction) {
        return invoiceService.findAllSorted(sortBy, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Invoice invoice;

        try {
            invoice = invoiceService.findById(id).orElse(null);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (invoice == null) {
            response.put("mensaje", "Factura ID: " + id + " no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Invoice invoice, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Invoice invoiceNew;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Client cliente = clientDao.findById(invoice.getCliente().getId()).orElse(null);
        if (cliente == null) {
            response.put("mensaje", "Cliente ID: " + invoice.getCliente().getId() + " no existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        invoice.setCliente(cliente);

        // ===== CÁLCULO DESCUENTO =====
        double porcentajeDescuento = 0.0;

        if (cliente.getDiscounts() != null) {
            for (Discount d : cliente.getDiscounts()) {
                porcentajeDescuento += d.getPorcentaje();
            }
        }

        BigDecimal subtotal = invoice.getPrecio()
                .multiply(new BigDecimal(invoice.getNoches()));

        BigDecimal descuento = subtotal
                .multiply(new BigDecimal(porcentajeDescuento / 100));

        BigDecimal subtotalConDescuento = subtotal.subtract(descuento);

        BigDecimal iva = subtotalConDescuento
                .multiply(new BigDecimal("0.10"));

        invoice.setSubtotal(subtotalConDescuento);
        invoice.setIva(iva);
        invoice.setTotal(subtotalConDescuento.add(iva));

        invoice.setPagada(false);

        try {
            invoiceNew = invoiceService.save(invoice);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar la factura en la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Factura creada con éxito");
        response.put("invoice", invoiceNew);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // =========================
    // UPDATE CON DESCUENTOS
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Invoice invoiceData, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Invoice currentInvoice = invoiceService.findById(id).orElse(null);

        if (currentInvoice == null) {
            response.put("mensaje", "Factura ID: " + id + " no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Client cliente = clientDao.findById(invoiceData.getCliente().getId()).orElse(null);
        if (cliente == null) {
            response.put("mensaje", "Cliente ID: " + invoiceData.getCliente().getId() + " no existe");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            currentInvoice.setCliente(cliente);
            currentInvoice.setConcepto(invoiceData.getConcepto());
            currentInvoice.setNoches(invoiceData.getNoches());
            currentInvoice.setPrecio(invoiceData.getPrecio());

            // ===== CÁLCULO DESCUENTO =====
            double porcentajeDescuento = 0.0;

            if (cliente.getDiscounts() != null) {
                for (Discount d : cliente.getDiscounts()) {
                    porcentajeDescuento += d.getPorcentaje();
                }
            }

            BigDecimal subtotal = invoiceData.getPrecio()
                    .multiply(new BigDecimal(invoiceData.getNoches()));

            BigDecimal descuento = subtotal
                    .multiply(new BigDecimal(porcentajeDescuento / 100));

            BigDecimal subtotalConDescuento = subtotal.subtract(descuento);

            BigDecimal iva = subtotalConDescuento
                    .multiply(new BigDecimal("0.10"));

            currentInvoice.setSubtotal(subtotalConDescuento);
            currentInvoice.setIva(iva);
            currentInvoice.setTotal(subtotalConDescuento.add(iva));

            currentInvoice.setPagada(invoiceData.isPagada());

            invoiceService.save(currentInvoice);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar la factura");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Factura actualizada con éxito");
        response.put("invoice", currentInvoice);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Invoice invoiceEliminar = invoiceService.findById(id).orElse(null);

        if (invoiceEliminar == null) {
            response.put("mensaje", "Factura ID: " + id + " no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            invoiceService.delete(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar la factura");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Factura eliminada con éxito");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}