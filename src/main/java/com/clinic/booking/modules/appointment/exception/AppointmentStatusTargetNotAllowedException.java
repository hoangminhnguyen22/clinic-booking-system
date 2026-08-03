package com.clinic.booking.modules.appointment.exception;

public class AppointmentStatusTargetNotAllowedException extends RuntimeException {
    public AppointmentStatusTargetNotAllowedException() {
        super("Appointment status can only be updated to COMPLETED or NO_SHOW.");
    }
}
