package com.gestionmediterraneo.hotel.daos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.AuditLog;

/**
 * Data Access Object for {@link AuditLog} entities.
 *
 * @author Gestión Mediterráneo
 */
public interface IAuditLogDAO extends JpaRepository<AuditLog, Long> {
}
