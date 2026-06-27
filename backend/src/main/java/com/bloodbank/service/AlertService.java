package com.bloodbank.service;

import com.bloodbank.model.Alert;
import com.bloodbank.model.Donor;
import com.bloodbank.model.Hospital;
import com.bloodbank.repository.AlertRepository;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final int COOLDOWN_MONTHS = 3;

    /**
     * Send an urgent blood alert to donors within a given radius.
     * Only donors who:
     *  - Are not in the 3-month cooldown
     *  - Have alerts enabled
     *  - Are within the specified radius
     *  - Match the required blood group
     * ...will be notified.
     */
    @Transactional
    public AlertResult sendUrgentAlert(Long hospitalId, String bloodGroup,
                                        String message, double radiusKm,
                                        String urgency, String unitsNeeded) {

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // Calculate bounding box for quick DB filter
        double latDelta = radiusKm / EARTH_RADIUS_KM * (180.0 / Math.PI);
        double lonDelta = radiusKm / (EARTH_RADIUS_KM * Math.cos(Math.toRadians(hospital.getLatitude())))
                          * (180.0 / Math.PI);

        double minLat = hospital.getLatitude() - latDelta;
        double maxLat = hospital.getLatitude() + latDelta;
        double minLon = hospital.getLongitude() - lonDelta;
        double maxLon = hospital.getLongitude() + lonDelta;

        LocalDate cooldownDate = LocalDate.now().minusMonths(COOLDOWN_MONTHS);

        List<Donor> candidateDonors = donorRepository.findNearbyDonorsByBloodGroup(
                bloodGroup, minLat, maxLat, minLon, maxLon, cooldownDate);

        // Precise Haversine filtering
        List<Donor> nearbyDonors = candidateDonors.stream()
                .filter(d -> haversineDistance(hospital.getLatitude(), hospital.getLongitude(),
                        d.getLatitude(), d.getLongitude()) <= radiusKm)
                .toList();

        // In a real app: send SMS/push notification to each donor
        // Here we simulate and log
        nearbyDonors.forEach(d -> {
            log.info("🚨 ALERT sent to donor: {} ({}) | Phone: {} | Distance: {:.1f} km",
                    d.getName(), d.getBloodGroup(), d.getPhone(),
                    haversineDistance(hospital.getLatitude(), hospital.getLongitude(),
                            d.getLatitude(), d.getLongitude()));
        });

        Alert alert = Alert.builder()
                .hospital(hospital)
                .bloodGroup(bloodGroup)
                .message(message)
                .radiusKm(radiusKm)
                .donorsNotified(nearbyDonors.size())
                .status("ACTIVE")
                .urgency(urgency != null ? urgency : "HIGH")
                .unitsNeeded(unitsNeeded)
                .build();

        Alert saved = alertRepository.save(alert);

        return new AlertResult(saved, nearbyDonors);
    }

    public List<Alert> getHospitalAlerts(Long hospitalId) {
        return alertRepository.findByHospitalIdOrderByCreatedAtDesc(hospitalId);
    }

    public List<Alert> getRecentAlerts() {
        return alertRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional
    public Alert resolveAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setStatus("RESOLVED");
        return alertRepository.save(alert);
    }

    // Haversine formula: distance between two lat/lon points in km
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    // DTO for alert result
    public record AlertResult(Alert alert, List<Donor> notifiedDonors) {}
}
