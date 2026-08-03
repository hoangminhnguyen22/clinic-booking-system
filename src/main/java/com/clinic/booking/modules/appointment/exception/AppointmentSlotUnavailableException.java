package com.clinic.booking.modules.appointment.exception;

public class AppointmentSlotUnavailableException extends RuntimeException {
    public AppointmentSlotUnavailableException() {
        super("The requested appointment slot is unavailable.");
    }
}
