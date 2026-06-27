package com.bloodbank.service;

import com.bloodbank.model.Hospital;
import com.bloodbank.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    @Transactional
    public Hospital registerHospital(Hospital hospital) {
        Optional<Hospital> existing = hospitalRepository.findByUsername(hospital.getUsername());
        if (existing.isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        return hospitalRepository.save(hospital);
    }

    public Optional<Hospital> login(String username, String password) {
        return hospitalRepository.findByUsername(username)
                .filter(h -> h.getPassword().equals(password));
    }

    public List<Hospital> searchByCity(String city) {
        return hospitalRepository.findByCityIgnoreCase(city);
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Optional<Hospital> getById(Long id) {
        return hospitalRepository.findById(id);
    }
}
