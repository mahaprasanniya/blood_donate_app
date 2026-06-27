package com.bloodbank.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(nullable = false)
    private String bloodGroup;

    @Column(nullable = false)
    private String message;

    // Radius in kilometers for nearby donor search
    @Column(nullable = false)
    private double radiusKm;

    // How many donors were notified
    private int donorsNotified;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Status: ACTIVE, RESOLVED, EXPIRED
    @Column(nullable = false)
    private String status = "ACTIVE";

    // Urgency level: LOW, MEDIUM, HIGH, CRITICAL
    @Column(nullable = false)
    private String urgency = "HIGH";

    private String unitsNeeded;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
