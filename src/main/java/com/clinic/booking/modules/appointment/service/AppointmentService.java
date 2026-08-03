package com.clinic.booking.modules.appointment.service;

import java.util.List;

import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
import com.clinic.booking.modules.appointment.dto.request.AppointmentStatusUpdateRequest;
import com.clinic.booking.modules.appointment.dto.response.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentCreateRequest request);

    List<AppointmentResponse> getAppointmentsForPatient(Long patientId);

    AppointmentResponse cancelAppointment(Long appointmentId);

    AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatusUpdateRequest request);
}
