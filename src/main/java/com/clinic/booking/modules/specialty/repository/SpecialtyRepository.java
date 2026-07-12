package com.clinic.booking.modules.specialty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinic.booking.modules.specialty.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    boolean existsByNameIgnoreCase(String name);
}
