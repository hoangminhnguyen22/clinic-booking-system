package com.clinic.booking.modules.doctor_schedule.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DoctorScheduleResponse(
        Long id,
        Long doctorId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime) {

}
