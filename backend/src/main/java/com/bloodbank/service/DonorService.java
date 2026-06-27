package com.bloodbank.service;

import com.bloodbank.model.Donor;
import com.bloodbank.model.DonationRecord;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.DonationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonorService {

    private final DonorRepository donorRepository;
    private final DonationRecordRepository donationRecordRepository;

    private static final int COOLDOWN_MONTHS = 3;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    // ── Register new donor ──────────────────────────────────────────────────
    @Transactional
    public Donor registerDonor(Donor donor) {
        // Check if phone already registered
        Optional<Donor> existing = donorRepository.findByPhoneAndDeletedFalse(donor.getPhone());
        if (existing.isPresent()) {
            throw new RuntimeException("Phone number already registered: " + donor.getPhone());
        }
        donor.setAvailable(true);
        donor.setDeleted(false);
        donor.setFailedContactAttempts(0);
        return donorRepository.save(donor);
    }

    // ── Get all available donors (not in cooldown) ──────────────────────────
    public List<Donor> getAvailableDonors() {
        LocalDate cooldownDate = LocalDate.now().minusMonths(COOLDOWN_MONTHS);
        return donorRepository.findAllAvailableDonors(cooldownDate);
    }

    // ── Search donors by blood group + city (hospital view) ────────────────
    public List<Donor> searchForHospital(String bloodGroup, String city) {
        List<Donor> donors = donorRepository.findForHospitalSearch(
                (city != null && city.isBlank()) ? null : city,
                (bloodGroup != null && bloodGroup.isBlank()) ? null : bloodGroup);

        // Filter: hide donors in cooldown
        return donors.stream()
                .filter(d -> !d.isInCooldown())
                .toList();
    }

    // ── Record a donation (triggers 3-month cooldown) ───────────────────────
    @Transactional
    public DonationRecord recordDonation(Long donorId, Long hospitalId, String notes) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        donor.setLastDonationDate(LocalDate.now());
        donor.setAvailable(true); // still available, just hidden until cooldown ends
        donorRepository.save(donor);

        DonationRecord record = DonationRecord.builder()
                .donor(donor)
                .donationDate(LocalDate.now())
                .bloodGroup(donor.getBloodGroup())
                .notes(notes)
                .build();

        // If hospitalId provided, link it
        if (hospitalId != null) {
            record.setHospital(null); // simplified; you can fetch hospital here
        }

        log.info("Donation recorded for donor: {} | Hidden until: {}",
                donor.getName(), donor.getCooldownEndDate());
        return donationRecordRepository.save(record);
    }

    // ── Increment failed contact attempts; auto-delete if >= 5 ─────────────
    @Transactional
    public Donor recordFailedContact(Long donorId) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        donor.setFailedContactAttempts(donor.getFailedContactAttempts() + 1);

        if (donor.getFailedContactAttempts() >= MAX_FAILED_ATTEMPTS) {
            donor.setDeleted(true);
            log.warn("Donor {} auto-deleted after {} failed contact attempts",
                    donor.getName(), MAX_FAILED_ATTEMPTS);
        }

        return donorRepository.save(donor);
    }

    // ── Get donor by ID ─────────────────────────────────────────────────────
    public Optional<Donor> getDonorById(Long id) {
        return donorRepository.findById(id);
    }

    // ── Donor updates their own info ────────────────────────────────────────
    @Transactional
    public Donor updateDonor(Long id, Donor updated) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        donor.setName(updated.getName());
        donor.setEmail(updated.getEmail());
        donor.setCity(updated.getCity());
        donor.setState(updated.getState());
        donor.setLatitude(updated.getLatitude());
        donor.setLongitude(updated.getLongitude());
        donor.setAlertsEnabled(updated.isAlertsEnabled());
        return donorRepository.save(donor);
    }

    // ── Soft-delete donor ───────────────────────────────────────────────────
    @Transactional
    public void deleteDonor(Long id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        donor.setDeleted(true);
        donorRepository.save(donor);
        log.info("Donor {} soft-deleted", donor.getName());
    }

    // ── Scheduled: auto-clean donors with 5+ failed attempts (daily) ───────
    @Scheduled(cron = "0 0 2 * * ?") // 2 AM every day
    @Transactional
    public void autoCleanFailedContacts() {
        List<Donor> toDelete = donorRepository
                .findByDeletedFalseAndFailedContactAttemptsGreaterThanEqual(MAX_FAILED_ATTEMPTS);
        toDelete.forEach(d -> d.setDeleted(true));
        donorRepository.saveAll(toDelete);
        if (!toDelete.isEmpty()) {
            log.info("Auto-cleaned {} donors with {} failed contact attempts", toDelete.size(), MAX_FAILED_ATTEMPTS);
        }
    }

    // ── Stats ───────────────────────────────────────────────────────────────
    public long getTotalActiveDonors() {
        return getAvailableDonors().size();
    }

    public List<DonationRecord> getDonorHistory(Long donorId) {
        return donationRecordRepository.findByDonorIdOrderByDonationDateDesc(donorId);
    }
}
