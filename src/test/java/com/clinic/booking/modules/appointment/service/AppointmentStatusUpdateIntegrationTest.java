package com.clinic.booking.modules.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.booking.modules.appointment.dto.request.AppointmentStatusUpdateRequest;
import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.appointment.repository.AppointmentRepository;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.specialty.entity.Specialty;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AppointmentStatusUpdateIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistCompletedStatusThroughDirtyChecking() {
        Specialty specialty = new Specialty("Cardiology");
        entityManager.persist(specialty);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Linh Pham");
        doctor.setEmail("linh.pham@example.com");
        doctor.setSpecialty(specialty);
        entityManager.persist(doctor);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatientId(10L);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setStartTime(LocalTime.of(8, 0));
        appointment.setEndTime(LocalTime.of(8, 30));
        appointment.setStatus(AppointmentStatus.BOOKED);
        entityManager.persist(appointment);
        entityManager.flush();

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.COMPLETED);

        appointmentService.updateAppointmentStatus(appointment.getId(), request);

        entityManager.flush();
        entityManager.clear();

        Appointment reloadedAppointment = appointmentRepository
                .findById(appointment.getId())
                .orElseThrow();

        assertEquals(
                AppointmentStatus.COMPLETED,
                reloadedAppointment.getStatus());
    }

    @Test
    void shouldPersistNoShowStatusThroughDirtyChecking() {
        Specialty specialty = new Specialty("Dermatology");
        entityManager.persist(specialty);

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Minh Tran");
        doctor.setEmail("minh.tran@example.com");
        doctor.setSpecialty(specialty);
        entityManager.persist(doctor);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatientId(11L);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setStartTime(LocalTime.of(9, 0));
        appointment.setEndTime(LocalTime.of(9, 30));
        appointment.setStatus(AppointmentStatus.BOOKED);
        entityManager.persist(appointment);
        entityManager.flush();

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.NO_SHOW);

        appointmentService.updateAppointmentStatus(appointment.getId(), request);

        entityManager.flush();
        entityManager.clear();

        Appointment reloadedAppointment = appointmentRepository
                .findById(appointment.getId())
                .orElseThrow();

        assertEquals(
                AppointmentStatus.NO_SHOW,
                reloadedAppointment.getStatus());
    }
}