package com.clinic.booking.modules.doctor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinic.booking.modules.doctor.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
