package com.clinic.booking.modules.appointment.dto.request;

import com.clinic.booking.modules.appointment.entity.AppointmentStatus;

import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateRequest(
        @NotNull(message = "Appointment status is required") AppointmentStatus status) {

}
