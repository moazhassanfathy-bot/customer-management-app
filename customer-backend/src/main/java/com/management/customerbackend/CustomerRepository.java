package com.management.customerbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Customer entity operations.
 * Extends JpaRepository to inherit complete CRUD and pagination capabilities from Spring Data JPA.
 */
@Repository
public interface CustomerRepository extends JpaRepository<com.management.customerbackend.Customer, Integer> {
    // No implementation required! Spring Data JPA automatically generates the underlying SQL queries.
}