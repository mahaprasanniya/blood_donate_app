package com.bloodbank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, unique = true)
    private String phone;

    @Email
    private String email;

    @NotBlank(message = "Blood group is required")
    @Column(nullable = false)
    private String bloodGroup;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    private double latitude;
    private double longitude;

    @Min(18) @Max(65)
    private int age;

    private String gender;

    // Last donation date — if within 3 months, donor is HIDDEN from hospitals
    private LocalDate lastDonationDate;

    // Whether donor is currently available (not hidden due to cooldown)
    @Column(nullable = false)
    private boolean available = true;

    // Failed contact attempts (auto-delete after 5)
    @Column(nullable = false)
    private int failedContactAttempts = 0;

    // Soft-delete flag
    @Column(nullable = false)
    private boolean deleted = false;

    // Registration date
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    // Donor's consent to receive alerts
    @Column(nullable = false)
    private boolean alertsEnabled = true;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    // Check if donor is in 3-month cooldown period
    public boolean isInCooldown() {
        if (lastDonationDate == null) return false;
        return lastDonationDate.plusMonths(3).isAfter(LocalDate.now());
    }

    // Get cooldown end date
    public LocalDate getCooldownEndDate() {
        if (lastDonationDate == null) return null;
        return lastDonationDate.plusMonths(3);
    }
}
