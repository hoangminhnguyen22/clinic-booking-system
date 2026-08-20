package com.clinic.booking.modules.appointment.service;

import java.util.List;

import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
import com.clinic.booking.modules.appointment.dto.request.AppointmentStatusUpdateRequest;
import com.clinic.booking.modules.appointment.dto.response.AppointmentResponse;
import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;

public interface AppointmentService {
    AppointmentResponse createAppointment(AuthenticatedActor actor, AppointmentCreateRequest request);

    List<AppointmentResponse> getAppointmentsForPatient(AuthenticatedActor actor);

    AppointmentResponse cancelAppointment(AuthenticatedActor actor, Long appointmentId);

    AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatusUpdateRequest request);
}
