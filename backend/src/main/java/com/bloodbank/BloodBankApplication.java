package com.bloodbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BloodBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(BloodBankApplication.class, args);
        System.out.println("\n============================================");
        System.out.println("  🩸 Blood Bank API started successfully!");
        System.out.println("  API:        http://localhost:8080/api");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("============================================\n");
    }
}
