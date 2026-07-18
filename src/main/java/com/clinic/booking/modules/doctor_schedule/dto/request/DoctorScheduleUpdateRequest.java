package com.clinic.booking.modules.doctor_schedule.dto.request;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record DoctorScheduleUpdateRequest(
        @NotNull(message = "Day of week is required") DayOfWeek dayOfWeek,
        @NotNull(message = "Start time is required") LocalTime startTime,
        @NotNull(message = "End time is required") LocalTime endTime) {
}
