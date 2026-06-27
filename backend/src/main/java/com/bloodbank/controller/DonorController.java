package com.bloodbank.controller;

import com.bloodbank.model.Donor;
import com.bloodbank.model.DonationRecord;
import com.bloodbank.service.DonorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;

    // POST /api/donors/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Donor donor) {
        try {
            Donor saved = donorService.registerDonor(donor);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Registration successful! Welcome to BloodConnect.",
                "donor", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // GET /api/donors/available
    @GetMapping("/available")
    public ResponseEntity<List<Donor>> getAvailable() {
        return ResponseEntity.ok(donorService.getAvailableDonors());
    }

    // GET /api/donors/search?bloodGroup=A+&city=Chennai
    @GetMapping("/search")
    public ResponseEntity<List<Donor>> search(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(donorService.searchForHospital(bloodGroup, city));
    }

    // GET /api/donors/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return donorService.getDonorById(id)
                .map(d -> ResponseEntity.ok((Object) d))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/donors/{id}/donate
    @PostMapping("/{id}/donate")
    public ResponseEntity<?> recordDonation(@PathVariable Long id,
                                             @RequestBody Map<String, Object> body) {
        try {
            Long hospitalId = body.containsKey("hospitalId") ?
                    Long.parseLong(body.get("hospitalId").toString()) : null;
            String notes = (String) body.getOrDefault("notes", "");
            DonationRecord record = donorService.recordDonation(id, hospitalId, notes);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Donation recorded. Donor will be hidden for 3 months for health recovery.",
                "record", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // POST /api/donors/{id}/failed-contact
    @PostMapping("/{id}/failed-contact")
    public ResponseEntity<?> failedContact(@PathVariable Long id) {
        try {
            Donor donor = donorService.recordFailedContact(id);
            String msg = donor.isDeleted()
                    ? "Donor auto-removed after 5 failed contact attempts."
                    : "Failed contact recorded. Attempts: " + donor.getFailedContactAttempts() + "/5";
            return ResponseEntity.ok(Map.of("success", true, "message", msg, "donor", donor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // PUT /api/donors/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Donor updated) {
        try {
            return ResponseEntity.ok(donorService.updateDonor(id, updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // DELETE /api/donors/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            donorService.deleteDonor(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Donor removed successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // GET /api/donors/{id}/history
    @GetMapping("/{id}/history")
    public ResponseEntity<List<DonationRecord>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(donorService.getDonorHistory(id));
    }
}
