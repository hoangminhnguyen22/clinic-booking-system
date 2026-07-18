package com.clinic.booking.modules.doctor_schedule.exception;

public class DoctorScheduleNotFoundException extends RuntimeException {
    public DoctorScheduleNotFoundException(Long id) {
        super("Doctor schedule not found with id: " + id);
    }
}
