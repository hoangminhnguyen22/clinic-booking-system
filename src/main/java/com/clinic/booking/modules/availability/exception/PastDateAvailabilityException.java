package com.clinic.booking.modules.availability.exception;

public class PastDateAvailabilityException extends RuntimeException {

    public PastDateAvailabilityException() {
        super("Availability date must not be in the past.");
    }
}