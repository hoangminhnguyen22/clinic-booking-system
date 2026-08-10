package com.clinic.booking.modules.appointment.mapper;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
import com.clinic.booking.modules.appointment.dto.response.AppointmentResponse;
import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.patient.entity.PatientProfile;

@Component
public class AppointmentMapper {
    public Appointment toEntity(AppointmentCreateRequest request, Doctor doctor, PatientProfile patient,
            LocalTime endTime) {
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(endTime);
        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getPatient().getId(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus());
    }
}
