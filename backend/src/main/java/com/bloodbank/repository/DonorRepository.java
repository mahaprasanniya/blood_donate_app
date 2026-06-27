package com.bloodbank.repository;

import com.bloodbank.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    // Find all non-deleted, available donors (not in cooldown)
    @Query("SELECT d FROM Donor d WHERE d.deleted = false AND d.available = true " +
           "AND (d.lastDonationDate IS NULL OR d.lastDonationDate < :cooldownDate)")
    List<Donor> findAllAvailableDonors(@Param("cooldownDate") LocalDate cooldownDate);

    // Find donors by blood group (available only)
    @Query("SELECT d FROM Donor d WHERE d.deleted = false AND d.available = true " +
           "AND d.bloodGroup = :bloodGroup " +
           "AND (d.lastDonationDate IS NULL OR d.lastDonationDate < :cooldownDate)")
    List<Donor> findByBloodGroup(@Param("bloodGroup") String bloodGroup,
                                  @Param("cooldownDate") LocalDate cooldownDate);

    // Find donors by city (available only)
    @Query("SELECT d FROM Donor d WHERE d.deleted = false AND d.available = true " +
           "AND LOWER(d.city) = LOWER(:city) " +
           "AND (d.lastDonationDate IS NULL OR d.lastDonationDate < :cooldownDate)")
    List<Donor> findByCityAvailable(@Param("city") String city,
                                     @Param("cooldownDate") LocalDate cooldownDate);

    // Find nearby donors within radius (using Haversine approximation via bounding box)
    @Query("SELECT d FROM Donor d WHERE d.deleted = false AND d.available = true " +
           "AND d.alertsEnabled = true " +
           "AND d.bloodGroup = :bloodGroup " +
           "AND (d.lastDonationDate IS NULL OR d.lastDonationDate < :cooldownDate) " +
           "AND d.latitude BETWEEN :minLat AND :maxLat " +
           "AND d.longitude BETWEEN :minLon AND :maxLon")
    List<Donor> findNearbyDonorsByBloodGroup(
            @Param("bloodGroup") String bloodGroup,
            @Param("minLat") double minLat, @Param("maxLat") double maxLat,
            @Param("minLon") double minLon, @Param("maxLon") double maxLon,
            @Param("cooldownDate") LocalDate cooldownDate);

    // Find donors with too many failed attempts
    List<Donor> findByDeletedFalseAndFailedContactAttemptsGreaterThanEqual(int attempts);

    // Find by phone
    Optional<Donor> findByPhoneAndDeletedFalse(String phone);

    // Find all (including cooldown ones) for hospital management view
    @Query("SELECT d FROM Donor d WHERE d.deleted = false " +
           "AND (:city IS NULL OR LOWER(d.city) = LOWER(:city)) " +
           "AND (:bloodGroup IS NULL OR d.bloodGroup = :bloodGroup)")
    List<Donor> findForHospitalSearch(@Param("city") String city,
                                       @Param("bloodGroup") String bloodGroup);
}
