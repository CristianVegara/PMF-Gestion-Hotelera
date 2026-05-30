package com.gestionmediterraneo.hotel.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Records an audit log entry for tracking system actions.
 *
 * <p>Each log entry captures the user who performed an action,
 * the type of entity affected, and optional details about the action.</p>
 *
 * @author Gestión Mediterráneo
 * @see User
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    /** Unique identifier for the audit log entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** User who performed the action. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"employee", "passwordHash"})
    private User user;

    /** Username of the user performing the action. */
    @Column(name = "username", nullable = false, length = 120)
    private String username;

    /** Description of the action performed. */
    @Column(name = "action", nullable = false, length = 80)
    private String action;

    /** Type of entity that was affected (e.g., "Client", "Booking"). */
    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;

    /** ID of the entity that was affected. */
    @Column(name = "entity_id")
    private Long entityId;

    /** Additional details about the action (truncated to 1000 chars). */
    @Column(name = "details", length = 1000)
    private String details;

    /** Timestamp when the log entry was created. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (username == null || username.isBlank()) {
            username = "system";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
