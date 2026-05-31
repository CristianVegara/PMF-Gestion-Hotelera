package com.gestionmediterraneo.hotel.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IAuditLogDAO;
import com.gestionmediterraneo.hotel.daos.IUserDAO;
import com.gestionmediterraneo.hotel.entities.AuditLog;
import com.gestionmediterraneo.hotel.entities.User;

/**
 * Service for recording audit log entries.
 *
 * <p>Automatically captures the current authenticated user's username
 * and stores the action, entity type, entity ID, and details.</p>
 *
 * @author Gestión Mediterráneo
 * @see AuditLog
 */
@Service
public class AuditLogService {

    /** Audit log DAO. */
    private final IAuditLogDAO auditLogDao;
    /** User DAO. */
    private final IUserDAO userDao;

    /**
     * Constructor for AuditLogService.
     * @param auditLogDao audit log data access object
     * @param userDao     user data access object
     */
    public AuditLogService(IAuditLogDAO auditLogDao, IUserDAO userDao) {
        this.auditLogDao = auditLogDao;
        this.userDao = userDao;
    }

    /**
     * Record an audit log entry.
     * @param action      description of the action performed
     * @param entityType  type of entity affected (e.g., "Client", "Booking")
     * @param entityId    ID of the affected entity
     * @param details     additional details about the action
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String entityType, Long entityId, String details) {
        try {
            String username = currentUsername();
            User user = userDao.findByUsername(username);

            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setUsername(username);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setDetails(limit(details));

            auditLogDao.save(log);
        } catch (Exception e) {
            System.err.println("No se pudo guardar auditoría: " + e.getMessage());
        }
    }

    /** Get the currently authenticated username, or "system" if unauthenticated. */
    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }

    /** Limit details string to 1000 characters. */
    private String limit(String details) {
        if (details == null) {
            return null;
        }
        return details.length() <= 1000 ? details : details.substring(0, 1000);
    }
}
