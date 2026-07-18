package com.clinic.booking.modules.doctor_schedule.exception;

public class OverlappingDoctorScheduleException extends RuntimeException {

    public OverlappingDoctorScheduleException() {
        super("Doctor already has an overlapping schedule for this day.");
    }
}
