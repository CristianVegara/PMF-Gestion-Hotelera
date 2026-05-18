package com.gestionmediterraneo.hotel.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestionmediterraneo.hotel.services.RevenueReportResponse;
import com.gestionmediterraneo.hotel.services.RevenueReportService;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000"})
public class RevenueController {

    @Autowired
    private RevenueReportService revenueReportService;

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String type) {

        try {
            RevenueReportResponse report = revenueReportService.calculateRevenue(from, to, roomId, clientId, type);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Error al generar el reporte de ingresos");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
