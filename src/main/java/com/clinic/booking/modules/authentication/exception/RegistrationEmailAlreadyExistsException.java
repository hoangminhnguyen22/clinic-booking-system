package com.clinic.booking.modules.authentication.exception;

public class RegistrationEmailAlreadyExistsException extends RuntimeException {
    public RegistrationEmailAlreadyExistsException() {
        super("Unable to create account with the supplied details.");
    }
}
