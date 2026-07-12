package com.clinic.booking.modules.specialty.exception;

public class SpecialtyAlreadyExistsException extends RuntimeException {

    public SpecialtyAlreadyExistsException(String name) {
        super("Specialty already exists: " + name);
    }
}
