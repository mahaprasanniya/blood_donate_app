package com.bloodbank.repository;

import com.bloodbank.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByUsername(String username);
    List<Hospital> findByCityIgnoreCase(String city);
    List<Hospital> findByStateIgnoreCase(String state);
}
