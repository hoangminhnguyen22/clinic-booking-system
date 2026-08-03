package com.clinic.booking.modules.appointment.exception;

public class AppointmentCancellationDeadlinePassedException extends RuntimeException {
    public AppointmentCancellationDeadlinePassedException() {
        super("Appointments cannot be cancelled at or after their start time.");
    }
}
