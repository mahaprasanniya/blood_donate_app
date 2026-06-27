package com.bloodbank.controller;

import com.bloodbank.model.Alert;
import com.bloodbank.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * POST /api/alerts/send
     * Body: { hospitalId, bloodGroup, message, radiusKm, urgency, unitsNeeded }
     * Sends alert only to donors within radiusKm who are available and have alerts enabled.
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendAlert(@RequestBody Map<String, Object> body) {
        try {
            Long hospitalId = Long.parseLong(body.get("hospitalId").toString());
            String bloodGroup = (String) body.get("bloodGroup");
            String message    = (String) body.get("message");
            double radiusKm   = Double.parseDouble(body.getOrDefault("radiusKm", "20").toString());
            String urgency    = (String) body.getOrDefault("urgency", "HIGH");
            String units      = (String) body.getOrDefault("unitsNeeded", "1");

            AlertService.AlertResult result = alertService.sendUrgentAlert(
                    hospitalId, bloodGroup, message, radiusKm, urgency, units);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Alert sent to " + result.notifiedDonors().size() + " nearby donors.",
                "alert", result.alert(),
                "donorsNotified", result.notifiedDonors().size(),
                "donors", result.notifiedDonors()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // GET /api/alerts/hospital/{hospitalId}
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<Alert>> getHospitalAlerts(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(alertService.getHospitalAlerts(hospitalId));
    }

    // GET /api/alerts/recent
    @GetMapping("/recent")
    public ResponseEntity<List<Alert>> getRecent() {
        return ResponseEntity.ok(alertService.getRecentAlerts());
    }

    // PUT /api/alerts/{id}/resolve
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id) {
        try {
            Alert alert = alertService.resolveAlert(id);
            return ResponseEntity.ok(Map.of("success", true, "alert", alert));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
