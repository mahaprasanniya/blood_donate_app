package com.bloodbank.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    private String address;
    private String phone;
    private String email;

    private double latitude;
    private double longitude;

    // Hospital login credentials (simple for demo)
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // In production: use BCrypt

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
}
