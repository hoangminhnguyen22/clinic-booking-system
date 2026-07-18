package com.clinic.booking.modules.doctor_schedule.exception;

public class InvalidScheduleTimeException extends RuntimeException {
    public InvalidScheduleTimeException() {
        super("Start time must be before end time.");
    }

}
