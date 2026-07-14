package com.clinic.booking.modules.doctor.service;

import java.util.List;

import com.clinic.booking.modules.doctor.dto.request.DoctorCreateRequest;
import com.clinic.booking.modules.doctor.dto.request.DoctorUpdateRequest;
import com.clinic.booking.modules.doctor.dto.response.DoctorResponse;

public interface DoctorService {

    DoctorResponse createDoctor(DoctorCreateRequest request);

    List<DoctorResponse> getAllDoctors();

    DoctorResponse getDoctorById(Long id);

    DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request);

    void deleteDoctor(Long id);
}
