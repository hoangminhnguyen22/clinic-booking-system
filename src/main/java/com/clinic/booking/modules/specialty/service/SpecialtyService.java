package com.clinic.booking.modules.specialty.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.clinic.booking.modules.specialty.dto.request.SpecialtyCreateRequest;
import com.clinic.booking.modules.specialty.dto.request.SpecialtyUpdateRequest;
import com.clinic.booking.modules.specialty.dto.response.SpecialtyResponse;

public interface SpecialtyService {
    List<SpecialtyResponse> getAll();

    SpecialtyResponse createSpecialty(SpecialtyCreateRequest request);

    SpecialtyResponse getSpecialtyById(@NonNull Long id);

    SpecialtyResponse updateSpecialty(@NonNull Long id, SpecialtyUpdateRequest request);

    void deleteSpecialty(@NonNull Long id);
}
