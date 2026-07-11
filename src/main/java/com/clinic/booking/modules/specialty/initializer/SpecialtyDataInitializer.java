package com.clinic.booking.modules.specialty.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.specialty.repository.SpecialtyRepository;

@Component
public class SpecialtyDataInitializer implements CommandLineRunner {
    private final SpecialtyRepository specialtyRepository;

    public SpecialtyDataInitializer(
            SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public void run(String... args) {
        if (specialtyRepository.count() == 0) {
            Specialty specialty = new Specialty("Cardiology2");
            specialtyRepository.save(specialty);
        }
    }
}
