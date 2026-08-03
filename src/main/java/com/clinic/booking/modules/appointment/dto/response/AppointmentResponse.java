package com.clinic.booking.modules.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.clinic.booking.modules.appointment.entity.AppointmentStatus;

public record AppointmentResponse(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status) {

}
