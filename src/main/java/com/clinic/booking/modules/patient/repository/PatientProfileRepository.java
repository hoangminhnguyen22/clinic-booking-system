package com.clinic.booking.modules.patient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinic.booking.modules.patient.entity.PatientProfile;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    Optional<PatientProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
