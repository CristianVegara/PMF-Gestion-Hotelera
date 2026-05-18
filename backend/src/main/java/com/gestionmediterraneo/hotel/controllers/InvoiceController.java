package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.services.BillingService;
import com.gestionmediterraneo.hotel.services.InvoiceGenerationRequest;
import com.gestionmediterraneo.hotel.services.InvoiceService;
import com.gestionmediterraneo.hotel.services.LoyaltyService;
import com.gestionmediterraneo.hotel.services.LoyaltyTier;
import com.gestionmediterraneo.hotel.services.RevenueReportResponse;
import com.gestionmediterraneo.hotel.services.RevenueReportService;
import com.gestionmediterraneo.hotel.daos.IClientDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private IClientDAO clientDao;

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private BillingService billingService;

    @Autowired
    private RevenueReportService revenueReportService;

    private static final Map<String, String> SORT_MAP = Map.of(
            "id", "id",
            "total", "total",
            "cliente", "cliente.nombre"
    );

    @GetMapping
    public ResponseEntity<?> getInvoices(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Map<String, Object> response = new HashMap<>();

        try {
            String mappedSort = SORT_MAP.getOrDefault(sortBy, "id");

            List<Invoice> invoices = invoiceService.findAllSorted(mappedSort, direction);

            response.put("data", invoices);
            response.put("error", null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("data", Collections.emptyList());
            response.put("mensaje", "Error al obtener facturas");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody InvoiceGenerationRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Invoice invoice = billingService.generateInvoice(request);
            response.put("mensaje", "Factura generada con éxito");
            response.put("data", invoice);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al generar la factura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Invoice invoice = invoiceService.findById(id).orElse(null);

            if (invoice == null) {
                response.put("mensaje", "Factura ID: " + id + " no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(invoice);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar la base de datos");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/report")
    public ResponseEntity<?> report(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String type) {

        Map<String, Object> response = new HashMap<>();
        try {
            RevenueReportResponse report = revenueReportService.calculateRevenue(from, to, roomId, clientId, type);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al generar el informe");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/client/{clientId}/expenses")
    public ResponseEntity<?> clientExpenses(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Client> clientOpt = Optional.ofNullable(clientDao.findById(clientId).orElse(null));
            if (clientOpt.isEmpty()) {
                response.put("mensaje", "Cliente no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<Invoice> invoices = invoiceService.findByClientBookingDateRange(clientId, from, to);
            BigDecimal totalSpent = invoices.stream()
                    .map(inv -> inv.getTotal() != null ? inv.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.put("client", clientOpt.get());
            response.put("from", from);
            response.put("to", to);
            response.put("invoiceCount", invoices.size());
            response.put("totalSpent", totalSpent);
            response.put("invoices", invoices);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al consultar gastos del cliente");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Invoice invoice, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Client cliente = clientDao.findById(invoice.getCliente().getId()).orElse(null);

            if (cliente == null) {
                response.put("mensaje", "Cliente no existe");
                return ResponseEntity.badRequest().body(response);
            }

            invoice.setCliente(cliente);
            applyLoyaltyDiscount(invoice, cliente);
            invoice.setPagada(false);

            Invoice saved = invoiceService.save(invoice);

            response.put("mensaje", "Factura creada con éxito");
            response.put("data", saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar factura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Invoice invoiceData, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Invoice current = invoiceService.findById(id).orElse(null);

            if (current == null) {
                response.put("mensaje", "Factura no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Client cliente = clientDao.findById(invoiceData.getCliente().getId()).orElse(null);

            if (cliente == null) {
                response.put("mensaje", "Cliente no existe");
                return ResponseEntity.badRequest().body(response);
            }

            current.setCliente(cliente);
            current.setConcepto(invoiceData.getConcepto());
            current.setNoches(invoiceData.getNoches());
            current.setPrecio(invoiceData.getPrecio());

            applyLoyaltyDiscount(current, cliente);
            current.setPagada(invoiceData.isPagada());

            Invoice updated = invoiceService.save(current);

            response.put("mensaje", "Factura actualizada");
            response.put("data", updated);

            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar factura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Invoice invoice = invoiceService.findById(id).orElse(null);

            if (invoice == null) {
                response.put("mensaje", "Factura no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            invoiceService.delete(id);

            response.put("mensaje", "Factura eliminada");
            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar factura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void applyLoyaltyDiscount(Invoice invoice, Client cliente) {
        LoyaltyTier tier = loyaltyService.calculateTier(cliente);

        BigDecimal subtotalBeforeDiscount = invoice.getPrecio()
                .multiply(BigDecimal.valueOf(invoice.getNoches()));

        BigDecimal discountPercentage = BigDecimal.valueOf(tier.getDiscountPercentage());

        BigDecimal discountAmount = subtotalBeforeDiscount
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100));

        BigDecimal subtotalWithDiscount = subtotalBeforeDiscount.subtract(discountAmount);

        BigDecimal iva = subtotalWithDiscount.multiply(new BigDecimal("0.10"));

        invoice.setSubtotalBeforeDiscount(subtotalBeforeDiscount);
        invoice.setDiscountPercentage(discountPercentage);
        invoice.setDiscountAmount(discountAmount);
        invoice.setLoyaltyRank(tier.getRank());
        invoice.setSubtotal(subtotalWithDiscount);
        invoice.setIva(iva);
        invoice.setTotal(subtotalWithDiscount.add(iva));
    }
}