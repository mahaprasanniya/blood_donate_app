package com.bloodbank.config;

import com.bloodbank.model.Donor;
import com.bloodbank.model.Hospital;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    public void run(String... args) throws Exception {
        seedHospitals();
        seedDonors();
        log.info("✅ Sample data seeded successfully!");
    }

    private void seedHospitals() {
        if (hospitalRepository.count() > 0) return;

        hospitalRepository.save(Hospital.builder()
                .name("Apollo Hospitals Chennai").city("Chennai").state("Tamil Nadu")
                .address("21, Greams Lane, Off Greams Road, Chennai - 600006")
                .phone("044-28293333").email("apollo.chennai@example.com")
                .latitude(13.0604).longitude(80.2496)
                .username("apollo_chennai").password("hospital123").build());

        hospitalRepository.save(Hospital.builder()
                .name("AIIMS Delhi").city("Delhi").state("Delhi")
                .address("Sri Aurobindo Marg, Ansari Nagar, New Delhi - 110029")
                .phone("011-26588500").email("aiims.delhi@example.com")
                .latitude(28.5672).longitude(77.2100)
                .username("aiims_delhi").password("hospital123").build());

        hospitalRepository.save(Hospital.builder()
                .name("Fortis Hospital Bangalore").city("Bangalore").state("Karnataka")
                .address("154/9, Bannerghatta Road, Bangalore - 560076")
                .phone("080-66214444").email("fortis.blr@example.com")
                .latitude(12.8908).longitude(77.5975)
                .username("fortis_blr").password("hospital123").build());

        hospitalRepository.save(Hospital.builder()
                .name("KGH Visakhapatnam").city("Visakhapatnam").state("Andhra Pradesh")
                .address("King George Hospital, Maharanipeta, Visakhapatnam - 530002")
                .phone("0891-2564891").email("kgh.vizag@example.com")
                .latitude(17.7041).longitude(83.2977)
                .username("kgh_vizag").password("hospital123").build());

        hospitalRepository.save(Hospital.builder()
                .name("MIOT International Chennai").city("Chennai").state("Tamil Nadu")
                .address("4/112, Mount Poonamallee Road, Manapakkam, Chennai - 600089")
                .phone("044-22490000").email("miot.chennai@example.com")
                .latitude(13.0140).longitude(80.1717)
                .username("miot_chennai").password("hospital123").build());

        log.info("🏥 Seeded 5 hospitals");
    }

    private void seedDonors() {
        if (donorRepository.count() > 0) return;

        // Chennai donors
        donorRepository.save(Donor.builder()
                .name("Arjun Ramesh").phone("9876543210").email("arjun@example.com")
                .bloodGroup("O+").city("Chennai").state("Tamil Nadu")
                .latitude(13.0827).longitude(80.2707).age(28).gender("Male")
                .available(true).alertsEnabled(true).build());

        donorRepository.save(Donor.builder()
                .name("Priya Venkat").phone("9876543211").email("priya@example.com")
                .bloodGroup("A+").city("Chennai").state("Tamil Nadu")
                .latitude(13.0604).longitude(80.2496).age(25).gender("Female")
                .available(true).alertsEnabled(true).build());

        donorRepository.save(Donor.builder()
                .name("Karthik Suresh").phone("9876543212").email("karthik@example.com")
                .bloodGroup("B+").city("Chennai").state("Tamil Nadu")
                .latitude(13.0500).longitude(80.2100).age(32).gender("Male")
                .lastDonationDate(LocalDate.now().minusMonths(4)) // donated 4 months ago → available
                .available(true).alertsEnabled(true).build());

        donorRepository.save(Donor.builder()
                .name("Meena Krishnan").phone("9876543213").email("meena@example.com")
                .bloodGroup("AB+").city("Chennai").state("Tamil Nadu")
                .latitude(13.0900).longitude(80.2800).age(29).gender("Female")
                .lastDonationDate(LocalDate.now().minusMonths(1)) // donated 1 month ago → IN COOLDOWN
                .available(true).alertsEnabled(true).build());

        donorRepository.save(Donor.builder()
                .name("Ravi Kumar").phone("9876543214").email("ravi@example.com")
                .bloodGroup("O-").city("Chennai").state("Tamil Nadu")
                .latitude(13.0700).longitude(80.2600).age(35).gender("Male")
                .available(true).alertsEnabled(true).build());

        // Delhi donors
        donorRepository.save(Donor.builder()
                .name("Amit Sharma").phone("9876543220").email("amit@example.com")
                .bloodGroup("A+").city("Delhi").state("Delhi")
                .latitude(28.6139).longitude(77.2090).age(27).gender("Male")
                .available(true).alertsEnabled(true).build());

        donorRepository.save(Donor.builder()
                .name("Sunita Gupta").phone("9876543221").email("sunita@example.com")
                .bloodGroup("B-").city("Delhi").state("Delhi")
                .latitude(28.5672).longitude(77.2100).age(30).gender("Female")
                .available(true).alertsEnabled(true).build());

        // Bangalore donors
        donorRepository.save(Donor.builder()
                .name("Vijay Nair").phone("9876543230").email("vijay@example.com")
                .bloodGroup("O+").city("Bangalore").state("Karnataka")
                .latitude(12.9716).longitude(77.5946).age(26).gender("Male")
                .available(true).alertsEnabled(true).build());

        donorRepository.save(Donor.builder()
                .name("Deepa Raj").phone("9876543231").email("deepa@example.com")
                .bloodGroup("A-").city("Bangalore").state("Karnataka")
                .latitude(12.8908).longitude(77.5975).age(24).gender("Female")
                .available(true).alertsEnabled(true).build());

        log.info("🩸 Seeded 9 sample donors");
    }
}
