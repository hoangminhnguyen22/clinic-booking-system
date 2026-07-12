package com.clinic.booking.modules.specialty.exception;

public class SpecialtyNotFoundException extends RuntimeException {

    public SpecialtyNotFoundException(Long id) {
        super("Specialty not found with id: " + id);
    }
}
