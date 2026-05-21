package com.management.customerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*") // مهمة جداً عشان تسمح لتطبيق الـ Desktop يتصل بدون مشاكل CORS
public class CustomerController {

    @Autowired
    private com.management.customerbackend.CustomerService service;

    @GetMapping
    public List<com.management.customerbackend.Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.management.customerbackend.Customer> getCustomerById(@PathVariable Integer id) {
        return service.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public com.management.customerbackend.Customer createCustomer(@RequestBody com.management.customerbackend.Customer customer) {
        return service.createCustomer(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.management.customerbackend.Customer> updateCustomer(@PathVariable Integer id, @RequestBody com.management.customerbackend.Customer customerDetails) {
        try {
            return ResponseEntity.ok(service.updateCustomer(id, customerDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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