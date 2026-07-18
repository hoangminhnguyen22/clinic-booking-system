package com.clinic.booking.modules.availability.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailableSlotResponse(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime) {
}
