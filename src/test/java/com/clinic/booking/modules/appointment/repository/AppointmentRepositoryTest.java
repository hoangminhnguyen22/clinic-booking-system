package com.clinic.booking.modules.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.specialty.entity.Specialty;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldRejectDuplicateDoctorDateAndStartTime() {
        Specialty specialty = new Specialty("General Medicine");
        entityManager.persist(specialty);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Anna Nguyen");
        doctor.setEmail("anna.nguyen@example.com");
        doctor.setSpecialty(specialty);
        entityManager.persist(doctor);
        entityManager.flush();

        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(8, 0);

        Appointment firstAppointment = createAppointment(
                doctor,
                10L,
                appointmentDate,
                startTime);

        appointmentRepository.saveAndFlush(firstAppointment);

        Appointment duplicateAppointment = createAppointment(
                doctor,
                20L,
                appointmentDate,
                startTime);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> appointmentRepository.saveAndFlush(duplicateAppointment));
    }

    private Appointment createAppointment(
            Doctor doctor,
            Long patientId,
            LocalDate appointmentDate,
            LocalTime startTime) {

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatientId(patientId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(startTime.plusMinutes(30));
        appointment.setStatus(AppointmentStatus.BOOKED);

        return appointment;
    }

    @Test
    void shouldAllowBookingSlotAfterPreviousAppointmentIsCancelled() {
        Specialty specialty = new Specialty("Dermatology");
        entityManager.persist(specialty);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Minh Tran");
        doctor.setEmail("minh.tran@example.com");
        doctor.setSpecialty(specialty);
        entityManager.persist(doctor);
        entityManager.flush();

        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);

        Appointment cancelledAppointment = createAppointment(
                doctor,
                10L,
                appointmentDate,
                startTime);
        cancelledAppointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.saveAndFlush(cancelledAppointment);

        Appointment bookedAppointment = createAppointment(
                doctor,
                20L,
                appointmentDate,
                startTime);

        assertDoesNotThrow(
                () -> appointmentRepository.saveAndFlush(bookedAppointment));
    }
}