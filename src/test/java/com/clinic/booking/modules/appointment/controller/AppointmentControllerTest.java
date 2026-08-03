package com.clinic.booking.modules.appointment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
import com.clinic.booking.modules.appointment.dto.response.AppointmentResponse;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.appointment.exception.AppointmentCancellationDeadlinePassedException;
import com.clinic.booking.modules.appointment.exception.AppointmentCancellationNotAllowedException;
import com.clinic.booking.modules.appointment.exception.AppointmentInPastException;
import com.clinic.booking.modules.appointment.exception.AppointmentNotFoundException;
import com.clinic.booking.modules.appointment.exception.AppointmentSlotUnavailableException;
import com.clinic.booking.modules.appointment.service.AppointmentService;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void shouldCreateAppointment() throws Exception {
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        AppointmentResponse response = new AppointmentResponse(
                1L,
                3L,
                10L,
                appointmentDate,
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                AppointmentStatus.BOOKED);

        when(appointmentService.createAppointment(
                any(AppointmentCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "doctorId": 3,
                          "patientId": 10,
                          "appointmentDate": "%s",
                          "startTime": "08:00"
                        }
                        """.formatted(appointmentDate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(3))
                .andExpect(jsonPath("$.patientId").value(10))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        verify(appointmentService).createAppointment(
                any(AppointmentCreateRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenDoctorIdIsMissing() throws Exception {
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "patientId": 10,
                          "appointmentDate": "%s",
                          "startTime": "08:00"
                        }
                        """.formatted(appointmentDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.validationErrors.doctorId")
                        .value("Doctor ID is required"));

        verifyNoInteractions(appointmentService);
    }

    @Test
    void shouldReturnConflictWhenAppointmentSlotIsUnavailable() throws Exception {
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        when(appointmentService.createAppointment(
                any(AppointmentCreateRequest.class)))
                .thenThrow(new AppointmentSlotUnavailableException());

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "doctorId": 3,
                          "patientId": 10,
                          "appointmentDate": "%s",
                          "startTime": "08:00"
                        }
                        """.formatted(appointmentDate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("The requested appointment slot is unavailable."));

        verify(appointmentService).createAppointment(
                any(AppointmentCreateRequest.class));
    }

    @Test
    void shouldReturnNotFoundWhenDoctorDoesNotExist() throws Exception {
        Long doctorId = 999L;
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        when(appointmentService.createAppointment(
                any(AppointmentCreateRequest.class)))
                .thenThrow(new DoctorNotFoundException(doctorId));

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "doctorId": %d,
                          "patientId": 10,
                          "appointmentDate": "%s",
                          "startTime": "08:00"
                        }
                        """.formatted(doctorId, appointmentDate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Doctor not found with id: 999"));

        verify(appointmentService).createAppointment(
                any(AppointmentCreateRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenAppointmentTimeIsInPast() throws Exception {
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        when(appointmentService.createAppointment(
                any(AppointmentCreateRequest.class)))
                .thenThrow(new AppointmentInPastException());

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "doctorId": 3,
                          "patientId": 10,
                          "appointmentDate": "%s",
                          "startTime": "08:00"
                        }
                        """.formatted(appointmentDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Appointment date and time must not be in the past."));

        verify(appointmentService).createAppointment(
                any(AppointmentCreateRequest.class));
    }

    @Test
    void shouldReturnAppointmentsForPatient() throws Exception {
        Long patientId = 10L;
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        AppointmentResponse response = new AppointmentResponse(
                1L,
                3L,
                patientId,
                appointmentDate,
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                AppointmentStatus.BOOKED);

        when(appointmentService.getAppointmentsForPatient(patientId))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/appointments")
                .param("patientId", patientId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(3))
                .andExpect(jsonPath("$[0].patientId").value(10))
                .andExpect(jsonPath("$[0].status").value("BOOKED"));

        verify(appointmentService).getAppointmentsForPatient(patientId);
    }

    @Test
    void shouldCancelAppointment() throws Exception {
        Long appointmentId = 1L;
        LocalDate appointmentDate = LocalDate.now().plusDays(1);

        AppointmentResponse response = new AppointmentResponse(
                appointmentId,
                3L,
                10L,
                appointmentDate,
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                AppointmentStatus.CANCELLED);

        when(appointmentService.cancelAppointment(appointmentId))
                .thenReturn(response);

        mockMvc.perform(patch("/api/appointments/{appointmentId}/cancel", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointmentId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(appointmentService).cancelAppointment(appointmentId);
    }

    @Test
    void shouldReturnNotFoundWhenCancellingAppointmentThatDoesNotExist() throws Exception {
        Long appointmentId = 999L;

        when(appointmentService.cancelAppointment(appointmentId))
                .thenThrow(new AppointmentNotFoundException(appointmentId));

        mockMvc.perform(patch("/api/appointments/{appointmentId}/cancel", appointmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Appointment not found with id: 999"));

        verify(appointmentService).cancelAppointment(appointmentId);
    }

    @Test
    void shouldReturnConflictWhenCancellingAppointmentThatIsNotBooked() throws Exception {
        Long appointmentId = 1L;

        when(appointmentService.cancelAppointment(appointmentId))
                .thenThrow(new AppointmentCancellationNotAllowedException());

        mockMvc.perform(patch("/api/appointments/{appointmentId}/cancel", appointmentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Only booked appointments can be cancelled."));

        verify(appointmentService).cancelAppointment(appointmentId);
    }

    @Test
    void shouldReturnConflictWhenCancellingAppointmentAtOrAfterStartTime() throws Exception {
        Long appointmentId = 1L;

        when(appointmentService.cancelAppointment(appointmentId))
                .thenThrow(new AppointmentCancellationDeadlinePassedException());

        mockMvc.perform(patch("/api/appointments/{appointmentId}/cancel", appointmentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Appointments cannot be cancelled at or after their start time."));

        verify(appointmentService).cancelAppointment(appointmentId);
    }
}