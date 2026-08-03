package com.clinic.booking.modules.appointment.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndAppointmentDateAndStartTimeAndStatus(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime startTime,
            AppointmentStatus status);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatus(
            Long doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    List<Appointment> findByPatientIdOrderByAppointmentDateAscStartTimeAsc(Long patientId);
}
