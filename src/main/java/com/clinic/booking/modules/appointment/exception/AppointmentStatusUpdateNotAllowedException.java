package com.clinic.booking.modules.appointment.exception;

public class AppointmentStatusUpdateNotAllowedException extends RuntimeException {
    public AppointmentStatusUpdateNotAllowedException() {
        super("Only booked appointments can have their status updated.");
    }
}
