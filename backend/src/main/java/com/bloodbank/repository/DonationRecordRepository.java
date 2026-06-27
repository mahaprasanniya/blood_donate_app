package com.bloodbank.repository;

import com.bloodbank.model.DonationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRecordRepository extends JpaRepository<DonationRecord, Long> {
    List<DonationRecord> findByDonorIdOrderByDonationDateDesc(Long donorId);
    List<DonationRecord> findByHospitalIdOrderByDonationDateDesc(Long hospitalId);
}
