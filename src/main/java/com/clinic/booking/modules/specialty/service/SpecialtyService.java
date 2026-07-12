package com.clinic.booking.modules.specialty.service;

import java.util.List;

import com.clinic.booking.modules.specialty.dto.request.SpecialtyCreateRequest;
import com.clinic.booking.modules.specialty.dto.request.SpecialtyUpdateRequest;
import com.clinic.booking.modules.specialty.dto.response.SpecialtyResponse;

public interface SpecialtyService {
    List<SpecialtyResponse> getAll();

    SpecialtyResponse createSpecialty(SpecialtyCreateRequest request);

    SpecialtyResponse getSpecialtyById(Long id);

    SpecialtyResponse updateSpecialty(Long id, SpecialtyUpdateRequest request);
}
