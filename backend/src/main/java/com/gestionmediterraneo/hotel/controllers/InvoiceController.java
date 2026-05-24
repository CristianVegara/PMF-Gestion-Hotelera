package com.gestionmediterraneo.hotel.controllers;

import com.gestionmediterraneo.hotel.entities.Client;
import com.gestionmediterraneo.hotel.entities.Invoice;
import com.gestionmediterraneo.hotel.services.AuditLogService;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private AuditLogService auditLogService;

    private static final Map<String, String> SORT_MAP = Map.of(
            "id", "id",
            "total", "total",
            "cliente.nombre", "cliente.nombre",
            "cliente", "cliente.nombre"
    );

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> generate(@RequestBody InvoiceGenerationRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Invoice invoice = billingService.generateInvoice(request);
            response.put("mensaje", "Factura generada con éxito");
            response.put("data", invoice);
            auditLogService.record(
                    "FACTURA_GENERADA",
                    "Invoice",
                    invoice.getId(),
                    "Factura generada para reserva ID " + request.getBookingId()
                            + " por total " + invoice.getTotal());
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
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
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

            Client client = clientOpt.get();
            Map<String, Object> clientData = new HashMap<>();
            clientData.put("id", client.getId());
            clientData.put("dni", client.getDni());
            clientData.put("nombre", client.getNombre());
            clientData.put("telefono", client.getTelefono());
            clientData.put("correo", client.getCorreo());

            List<Map<String, Object>> invoiceData = invoices.stream()
                    .map(invoice -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", invoice.getId());
                        item.put("fechaEmision", invoice.getFechaEmision());
                        item.put("concepto", invoice.getConcepto());
                        item.put("noches", invoice.getNoches());
                        item.put("precio", invoice.getPrecio());
                        item.put("subtotalBeforeDiscount", invoice.getSubtotalBeforeDiscount());
                        item.put("discountPercentage", invoice.getDiscountPercentage());
                        item.put("discountAmount", invoice.getDiscountAmount());
                        item.put("loyaltyRank", invoice.getLoyaltyRank());
                        item.put("subtotal", invoice.getSubtotal());
                        item.put("iva", invoice.getIva());
                        item.put("total", invoice.getTotal());
                        item.put("pagada", invoice.isPagada());
                        item.put("status", invoice.getStatus());

                        if (invoice.getBooking() != null) {
                            Map<String, Object> bookingData = new HashMap<>();
                            bookingData.put("id", invoice.getBooking().getId());
                            bookingData.put("fechaEntrada", invoice.getBooking().getFechaEntrada());
                            bookingData.put("fechaSalida", invoice.getBooking().getFechaSalida());
                            item.put("booking", bookingData);
                        }

                        if (invoice.getHabitacion() != null) {
                            Map<String, Object> roomData = new HashMap<>();
                            roomData.put("id", invoice.getHabitacion().getId());
                            roomData.put("number", invoice.getHabitacion().getNumber());
                            item.put("habitacion", roomData);
                        }

                        return item;
                    })
                    .collect(Collectors.toList());

            response.put("client", clientData);
            response.put("from", from);
            response.put("to", to);
            response.put("invoiceCount", invoices.size());
            response.put("totalSpent", totalSpent);
            response.put("invoices", invoiceData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al consultar gastos del cliente");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/client/by-dni/{dni}/expenses")
    @PreAuthorize("hasAnyRole('USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> clientExpensesByDni(
            @PathVariable String dni,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Client client = clientDao.findByDni(dni);
        if (client == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Cliente no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return clientExpenses(client.getId(), from, to);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
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
            auditLogService.record(
                    "FACTURA_CREADA",
                    "Invoice",
                    saved.getId(),
                    "Factura manual creada para cliente ID " + cliente.getId()
                            + " por total " + saved.getTotal());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar factura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
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
            auditLogService.record(
                    "FACTURA_ACTUALIZADA",
                    "Invoice",
                    updated.getId(),
                    "Factura actualizada para cliente ID " + cliente.getId()
                            + " por total " + updated.getTotal());

            return ResponseEntity.ok(response);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar factura");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
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
            auditLogService.record(
                    "FACTURA_ELIMINADA",
                    "Invoice",
                    id,
                    "Factura eliminada");
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
