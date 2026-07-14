package com.clinic.booking.modules.doctor.exception;

public class DuplicateDoctorEmailException
        extends RuntimeException {

    public DuplicateDoctorEmailException(String email) {
        super("Doctor email already exists: " + email);
    }
}