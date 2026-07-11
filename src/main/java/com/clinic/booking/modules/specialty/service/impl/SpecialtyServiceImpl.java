package com.clinic.booking.modules.specialty.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.booking.modules.specialty.dto.response.SpecialtyResponse;
import com.clinic.booking.modules.specialty.mapper.SpecialtyMapper;
import com.clinic.booking.modules.specialty.repository.SpecialtyRepository;
import com.clinic.booking.modules.specialty.service.SpecialtyService;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public List<SpecialtyResponse> getAll() {
        return specialtyRepository.findAll()
                .stream()
                .map(specialtyMapper::toResponse)
                .toList();
    }
}
