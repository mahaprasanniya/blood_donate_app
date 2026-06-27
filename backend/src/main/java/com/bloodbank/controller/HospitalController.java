package com.bloodbank.controller;

import com.bloodbank.model.Hospital;
import com.bloodbank.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // POST /api/hospitals/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Hospital hospital) {
        try {
            Hospital saved = hospitalService.registerHospital(hospital);
            return ResponseEntity.ok(Map.of("success", true, "hospital", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // POST /api/hospitals/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        return hospitalService.login(creds.get("username"), creds.get("password"))
                .map(h -> ResponseEntity.ok((Object) Map.of("success", true, "hospital", h)))
                .orElse(ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials")));
    }

    // GET /api/hospitals/search?city=Chennai
    @GetMapping("/search")
    public ResponseEntity<List<Hospital>> search(@RequestParam String city) {
        return ResponseEntity.ok(hospitalService.searchByCity(city));
    }

    // GET /api/hospitals
    @GetMapping
    public ResponseEntity<List<Hospital>> getAll() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    // GET /api/hospitals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return hospitalService.getById(id)
                .map(h -> ResponseEntity.ok((Object) h))
                .orElse(ResponseEntity.notFound().build());
    }
}
