package com.clinic.booking.modules.doctor.exception;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(Long id) {
        super("Doctor not found with id: " + id);
    }
}
