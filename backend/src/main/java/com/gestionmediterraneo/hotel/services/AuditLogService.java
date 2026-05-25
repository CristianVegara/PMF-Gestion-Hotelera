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

@Service
public class AuditLogService {

    private final IAuditLogDAO auditLogDao;
    private final IUserDAO userDao;

    public AuditLogService(IAuditLogDAO auditLogDao, IUserDAO userDao) {
        this.auditLogDao = auditLogDao;
        this.userDao = userDao;
    }

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

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }

    private String limit(String details) {
        if (details == null) {
            return null;
        }
        return details.length() <= 1000 ? details : details.substring(0, 1000);
    }
}
