package com.clinic.booking.modules.appointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
import com.clinic.booking.modules.appointment.dto.request.AppointmentStatusUpdateRequest;
import com.clinic.booking.modules.appointment.dto.response.AppointmentResponse;
import com.clinic.booking.modules.appointment.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse createAppointment(
            @AuthenticationPrincipal AuthenticatedActor actor,
            @Valid @RequestBody AppointmentCreateRequest request) {

        return appointmentService.createAppointment(actor, request);
    }

    @GetMapping
    public List<AppointmentResponse> getAppointmentsForPatient(
            @AuthenticationPrincipal AuthenticatedActor actor) {
        return appointmentService.getAppointmentsForPatient(actor);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(
            @AuthenticationPrincipal AuthenticatedActor actor,
            @PathVariable("appointmentId") Long appointmentId) {

        return appointmentService.cancelAppointment(actor, appointmentId);
    }

    @PatchMapping("/{appointmentId}/status")
    public AppointmentResponse updateAppointmentStatus(
            @PathVariable("appointmentId") Long appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {

        return appointmentService.updateAppointmentStatus(appointmentId, request);
    }
}
