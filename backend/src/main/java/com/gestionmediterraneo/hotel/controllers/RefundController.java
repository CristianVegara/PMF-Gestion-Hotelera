package com.gestionmediterraneo.hotel.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gestionmediterraneo.hotel.daos.IRefundDAO;
import com.gestionmediterraneo.hotel.entities.Refund;

@RestController
@RequestMapping("/api/refunds")
@CrossOrigin(origins = {"http://localhost:3000"})
public class RefundController {

    @Autowired
    private IRefundDAO refundDao;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<?> createRefund(@RequestBody Refund refund) {
        Map<String, Object> response = new HashMap<>();
        try {
            Refund saved = refundDao.save(refund);
            response.put("mensaje", "Reembolso registrado con éxito");
            response.put("data", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al guardar reembolso");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
