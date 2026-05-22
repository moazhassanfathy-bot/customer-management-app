package com.management.customerbackend;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing the "customers" table in the database.
 * Mapped using JPA annotations to handle Object-Relational Mapping (ORM).
 */
@Entity
@Table(name = "customers")
public class Customer {

    /** Primary key with auto-increment strategy. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Customer's full name, mandatory field, max length 100. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Customer's email address, mandatory field, max length 100. */
    @Column(nullable = false, length = 100)
    private String email;

    /** Customer's phone number, optional field, max length 50. */
    @Column(nullable = false ,length = 50)
    private String phone;

    /** Timestamp when the record is created. It is non-updatable after generation. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA Lifecycle callback method.
     * Automatically sets the createdAt timestamp right before persisting the record to the database.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==========================================
    // Getters and Setters
    // Required for Jackson JSON serialization and Hibernate ORM framework
    // ==========================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        // Used by Hibernate when loading existing records from DB
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}