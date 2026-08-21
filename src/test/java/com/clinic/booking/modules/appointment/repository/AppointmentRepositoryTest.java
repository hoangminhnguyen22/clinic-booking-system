package com.clinic.booking.modules.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
import com.clinic.booking.modules.patient.entity.PatientProfile;
import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.user.entity.User;

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

        User user = new User();
        user.setEmail("patient.one@example.com");
        user.setPasswordHash("test-password-hash");
        entityManager.persist(user);

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        entityManager.persist(patientProfile);

        entityManager.flush();

        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(8, 0);

        Appointment firstAppointment = createAppointment(
                doctor,
                patientProfile.getId(),
                appointmentDate,
                startTime);

        appointmentRepository.saveAndFlush(firstAppointment);

        Appointment duplicateAppointment = createAppointment(
                doctor,
                patientProfile.getId(),
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

        User firstUser = new User();
        firstUser.setEmail("cancelled.patient@example.com");
        firstUser.setPasswordHash("test-password-hash");
        entityManager.persist(firstUser);

        PatientProfile firstPatientProfile = new PatientProfile();
        firstPatientProfile.setUser(firstUser);
        entityManager.persist(firstPatientProfile);

        User secondUser = new User();
        secondUser.setEmail("new.patient@example.com");
        secondUser.setPasswordHash("test-password-hash");
        entityManager.persist(secondUser);

        PatientProfile secondPatientProfile = new PatientProfile();
        secondPatientProfile.setUser(secondUser);
        entityManager.persist(secondPatientProfile);

        entityManager.flush();

        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(9, 0);

        Appointment cancelledAppointment = createAppointment(
                doctor,
                firstPatientProfile.getId(),
                appointmentDate,
                startTime);
        cancelledAppointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.saveAndFlush(cancelledAppointment);

        Appointment bookedAppointment = createAppointment(
                doctor,
                secondPatientProfile.getId(),
                appointmentDate,
                startTime);

        assertDoesNotThrow(
                () -> appointmentRepository.saveAndFlush(bookedAppointment));
    }

    @Test
    void shouldReturnOnlyAppointmentsOwnedByRequestedPatient() {
        Specialty specialty = new Specialty("Cardiology");
        entityManager.persist(specialty);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Lan Pham");
        doctor.setEmail("lan.pham@example.com");
        doctor.setSpecialty(specialty);
        entityManager.persist(doctor);

        User firstUser = new User();
        firstUser.setEmail("first.owner@example.com");
        firstUser.setPasswordHash("test-password-hash");
        entityManager.persist(firstUser);

        PatientProfile firstPatientProfile = new PatientProfile();
        firstPatientProfile.setUser(firstUser);
        entityManager.persist(firstPatientProfile);

        User secondUser = new User();
        secondUser.setEmail("second.owner@example.com");
        secondUser.setPasswordHash("test-password-hash");
        entityManager.persist(secondUser);

        PatientProfile secondPatientProfile = new PatientProfile();
        secondPatientProfile.setUser(secondUser);
        entityManager.persist(secondPatientProfile);

        entityManager.flush();

        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        Appointment firstAppointment = createAppointment(
                doctor,
                firstPatientProfile.getId(),
                appointmentDate,
                LocalTime.of(10, 0));

        Appointment secondAppointment = createAppointment(
                doctor,
                secondPatientProfile.getId(),
                appointmentDate,
                LocalTime.of(11, 0));

        appointmentRepository.saveAndFlush(firstAppointment);
        appointmentRepository.saveAndFlush(secondAppointment);

        List<Appointment> appointments = appointmentRepository
                .findByPatientIdOrderByAppointmentDateAscStartTimeAsc(
                        firstPatientProfile.getId());

        assertEquals(1, appointments.size());
        assertEquals(firstAppointment.getId(), appointments.get(0).getId());
        assertEquals(
                firstPatientProfile.getId(),
                appointments.get(0).getPatientId());
    }
}