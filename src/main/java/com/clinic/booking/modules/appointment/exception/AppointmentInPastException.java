package com.clinic.booking.modules.appointment.exception;

public class AppointmentInPastException extends RuntimeException {
    public AppointmentInPastException() {
        super("Appointment date and time must not be in the past.");
    }
}
