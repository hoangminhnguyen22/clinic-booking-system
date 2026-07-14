package com.clinic.booking.modules.doctor.mapper;

import org.springframework.stereotype.Component;

import org.springframework.lang.NonNull;

import com.clinic.booking.modules.doctor.dto.request.DoctorCreateRequest;
import com.clinic.booking.modules.doctor.dto.request.DoctorUpdateRequest;
import com.clinic.booking.modules.doctor.dto.response.DoctorResponse;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.specialty.entity.Specialty;

@Component
public class DoctorMapper {

    // Nhận vào Doctor Entity
    // Trả về DoctorResponse
    public DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFullName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getBio(),
                doctor.isActive(),
                doctor.getSpecialty().getId(),
                doctor.getSpecialty().getName());
    }

    @NonNull
    public Doctor toEntity(
            DoctorCreateRequest request,
            Specialty specialty) {

        Doctor doctor = new Doctor();

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setBio(request.bio());
        doctor.setActive(true);
        doctor.setSpecialty(specialty);

        return doctor;
    }

    public void updateEntity(
            Doctor doctor,
            DoctorUpdateRequest request,
            Specialty specialty) {

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setBio(request.bio());
        doctor.setActive(request.active());
        doctor.setSpecialty(specialty);
    }
}
