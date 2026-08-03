ALTER TABLE appointments
    DROP CONSTRAINT IF EXISTS uk_appointments_doctor_date_start_time;

CREATE UNIQUE INDEX uk_booked_appointment_slot
    ON appointments (doctor_id, appointment_date, start_time)
    WHERE status = 'BOOKED';