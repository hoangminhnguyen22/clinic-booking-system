package com.clinic.booking.modules.specialty.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.specialty.repository.SpecialtyRepository;
import com.clinic.booking.modules.specialty.service.SpecialtyService;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public List<Specialty> getAll() {
        return specialtyRepository.findAll();
    }
}
