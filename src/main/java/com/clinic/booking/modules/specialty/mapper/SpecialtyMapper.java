package com.clinic.booking.modules.specialty.mapper;

import org.springframework.stereotype.Component;

import com.clinic.booking.modules.specialty.dto.request.SpecialtyCreateRequest;
import com.clinic.booking.modules.specialty.dto.response.SpecialtyResponse;
import com.clinic.booking.modules.specialty.entity.Specialty;

@Component
public class SpecialtyMapper {
    public SpecialtyResponse toResponse(Specialty specialty) {
        return new SpecialtyResponse(
                specialty.getId(),
                specialty.getName());
    }

    public Specialty toEntity(SpecialtyCreateRequest request) {
        return new Specialty(
                request.name());
    }
}
