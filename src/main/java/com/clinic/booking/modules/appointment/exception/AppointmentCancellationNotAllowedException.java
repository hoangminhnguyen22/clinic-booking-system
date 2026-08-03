package com.clinic.booking.modules.appointment.exception;

public class AppointmentCancellationNotAllowedException extends RuntimeException {
    public AppointmentCancellationNotAllowedException() {
        super("Only booked appointments can be cancelled.");
    }

}
