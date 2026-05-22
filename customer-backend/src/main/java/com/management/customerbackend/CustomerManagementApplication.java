package com.management.customerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Spring Boot Backend application.
 * This class bootstrap and initializes the entire application context, embedded server, and configurations.
 */
@SpringBootApplication
public class CustomerManagementApplication {

    /**
     * Main method that serves as the execution starting point for the JVM.
     * Launches the Spring application context using SpringApplication.run.
     * * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerManagementApplication.class, args);
    }

}