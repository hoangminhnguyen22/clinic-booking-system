package com.clinic.booking.modules.appointment.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AppointmentCreateRequest(
        @NotNull(message = "Doctor ID is required") @Positive Long doctorId,
        @NotNull(message = "Patient ID is required") @Positive Long patientId,
        @NotNull(message = "Appointment Date is required") LocalDate appointmentDate,
        @NotNull(message = "Start time is required") LocalTime startTime) {
}
