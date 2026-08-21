ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_patient_profile
        FOREIGN KEY (patient_id)
        REFERENCES patient_profiles (id);