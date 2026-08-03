package com.clinic.booking.modules.appointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
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
            @Valid @RequestBody AppointmentCreateRequest request) {

        return appointmentService.createAppointment(request);
    }

    @GetMapping
    public List<AppointmentResponse> getAppointmentsForPatient(@RequestParam("patientId") Long patientId) {
        return appointmentService.getAppointmentsForPatient(patientId);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable("appointmentId") Long appointmentId) {

        return appointmentService.cancelAppointment(appointmentId);
    }
}
