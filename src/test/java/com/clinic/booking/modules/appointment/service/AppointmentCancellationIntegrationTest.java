package com.clinic.booking.modules.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.appointment.repository.AppointmentRepository;
import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.patient.entity.PatientProfile;
import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AppointmentCancellationIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCancelledStatusThroughDirtyChecking() {
        User user = new User();
        user.setEmail("patient@example.com");
        user.setPasswordHash("test-password-hash");
        user.setRoles(Set.of(Role.PATIENT));
        entityManager.persist(user);

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        entityManager.persist(patientProfile);

        Specialty specialty = new Specialty("Cardiology");
        entityManager.persist(specialty);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Linh Pham");
        doctor.setEmail("linh.pham@example.com");
        doctor.setSpecialty(specialty);
        entityManager.persist(doctor);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatientId(patientProfile.getId());
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setStartTime(LocalTime.of(8, 0));
        appointment.setEndTime(LocalTime.of(8, 30));
        appointment.setStatus(AppointmentStatus.BOOKED);
        entityManager.persist(appointment);
        entityManager.flush();

        AuthenticatedActor actor = new AuthenticatedActor(
                user.getId(),
                Set.of(Role.PATIENT));

        appointmentService.cancelAppointment(actor, appointment.getId());

        entityManager.flush();
        entityManager.clear();

        Appointment reloadedAppointment = appointmentRepository
                .findById(appointment.getId())
                .orElseThrow();

        assertEquals(
                AppointmentStatus.CANCELLED,
                reloadedAppointment.getStatus());
    }
}
