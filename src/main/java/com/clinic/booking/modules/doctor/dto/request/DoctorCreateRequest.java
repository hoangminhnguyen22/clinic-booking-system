package com.clinic.booking.modules.doctor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record DoctorCreateRequest(

        @NotBlank(message = "Full name is required") @Size(max = 150, message = "Full name must not exceed 150 characters") String fullName,

        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") @Size(max = 150, message = "Email must not exceed 150 characters") String email,

        @Pattern(regexp = "^0[35789]\\d{8}$", message = "Phone must be a valid Vietnamese mobile number") String phone,

        String bio,

        @NotNull(message = "Specialty ID is required") Long specialtyId,

        @Min(value = 5, message = "Appointment duration must be at least 5 minutes") @Max(value = 120, message = "Appointment duration must not exceed 120 minutes") Integer appointmentDurationMinutes) {
}
