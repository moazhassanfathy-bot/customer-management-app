package com.management.customerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller that exposes HTTP endpoints for managing Customer resources.
 * Acts as the entry point for requests coming from the desktop application.
 */
@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*") // Crucial for allowing connection from the desktop client without CORS blocking
public class CustomerController {

    @Autowired
    private CustomerService service;

    /**
     * Retrieves a list of all existing customers from the database.
     * * @return List of Customer entities
     */
    @GetMapping
    public List<Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    /**
     * Retrieves a specific customer profile filtered by their unique identification number.
     * * @param id The identifier of the targeted customer
     * @return ResponseEntity containing the Customer if found (200 OK), or 404 Not Found status
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        return service.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Handles HTTP POST requests to create and persist a new customer record.
     * * @param customer The JSON payload representing the new customer
     * @return The freshly persisted Customer entity with its generated database ID
     */
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return service.createCustomer(customer);
    }

    /**
     * Modifies fields of an already existing customer matching the provided identifier.
     * * @param id              The identifier of the customer record to alter
     * @param customerDetails The new values mapped inside the JSON payload request
     * @return ResponseEntity containing updated Customer entity (200 OK), or 404 Not Found if id doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity <Customer> updateCustomer(@PathVariable Integer id, @RequestBody Customer customerDetails) {
        try {
            return ResponseEntity.ok(service.updateCustomer(id, customerDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Destroys a customer entity completely from the database using its unique identifier.
     * * @param id The identifier of the customer record to delete
     * @return ResponseEntity with 204 No Content status upon successful operation, or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        try {
            service.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}