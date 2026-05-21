package com.management.customerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private com.management.customerbackend.CustomerRepository repository;

    public List<com.management.customerbackend.Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Optional<com.management.customerbackend.Customer> getCustomerById(Integer id) {
        return repository.findById(id);
    }

    public com.management.customerbackend.Customer createCustomer(com.management.customerbackend.Customer customer) {
        return repository.save(customer);
    }

    public com.management.customerbackend.Customer updateCustomer(Integer id, com.management.customerbackend.Customer customerDetails) {
        com.management.customerbackend.Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());

        return repository.save(customer);
    }

    public void deleteCustomer(Integer id) {
        com.management.customerbackend.Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        repository.delete(customer);
    }
}