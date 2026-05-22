package com.management.customerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class that handles the core business logic for Customer operations.
 * It acts as an intermediary layer between the REST Controller and the JPA Repository.
 */
@Service
public class CustomerService {

    @Autowired
    private com.management.customerbackend.CustomerRepository repository;

    /**
     * Fetches all customer records available in the database.
     * * @return List of all Customer entities
     */
    public List<com.management.customerbackend.Customer> getAllCustomers() {
        return repository.findAll();
    }

    /**
     * Finds a single customer record using their unique database ID.
     * Wrapped in an Optional container to cleanly handle non-existing records.
     * * @param id The identifier of the customer
     * @return An Optional containing the Customer if found, or empty Optional if not
     */
    public Optional<com.management.customerbackend.Customer> getCustomerById(Integer id) {
        return repository.findById(id);
    }

    /**
     * Persists a newly created customer profile into the database.
     * * @param customer The new customer entity payload received
     * @return The saved Customer entity including its generated ID
     */
    public com.management.customerbackend.Customer createCustomer(com.management.customerbackend.Customer customer) {
        return repository.save(customer);
    }

    /**
     * Updates the fields of an existing customer record after retrieving it.
     * Throws an exception if the specified ID is not found.
     * * @param id              The identifier of the customer to modify
     * @param customerDetails The updated field values container
     * @return The updated and saved Customer entity
     * @throws RuntimeException If no customer record matches the provided ID
     */
    public com.management.customerbackend.Customer updateCustomer(Integer id, com.management.customerbackend.Customer customerDetails) {
        com.management.customerbackend.Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Mapping updated values to the managed entity
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());

        return repository.save(customer);
    }

    /**
     * Deletes a customer completely from the database after verifying their existence.
     * Throws an exception if the specified ID is not found.
     * * @param id The identifier of the customer to remove
     * @throws RuntimeException If no customer record matches the provided ID
     */
    public void deleteCustomer(Integer id) {
        com.management.customerbackend.Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        repository.delete(customer);
    }
}