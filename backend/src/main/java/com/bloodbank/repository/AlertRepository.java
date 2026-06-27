package com.bloodbank.repository;

import com.bloodbank.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByHospitalIdOrderByCreatedAtDesc(Long hospitalId);
    List<Alert> findByStatusOrderByCreatedAtDesc(String status);
    List<Alert> findTop10ByOrderByCreatedAtDesc();
}
