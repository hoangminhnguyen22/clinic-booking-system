package com.clinic.booking.modules.appointment.exception;

public class AppointmentStatusUpdateBeforeStartTimeException extends RuntimeException {
    public AppointmentStatusUpdateBeforeStartTimeException() {
        super("Appointment status can only be updated at or after its start time.");
    }
}
