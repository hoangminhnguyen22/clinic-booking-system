package com.clinic.booking.modules.doctor.dto.response;

public record DoctorResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String bio,
        boolean active,
        Long specialtyId,
        String specialtyName,
        Integer appointmentDurationMinutes) {
}
