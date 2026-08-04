package com.clinic.booking.modules.appointment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.clinic.booking.modules.appointment.dto.request.AppointmentCreateRequest;
import com.clinic.booking.modules.appointment.dto.request.AppointmentStatusUpdateRequest;
import com.clinic.booking.modules.appointment.dto.response.AppointmentResponse;
import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.appointment.exception.AppointmentCancellationDeadlinePassedException;
import com.clinic.booking.modules.appointment.exception.AppointmentCancellationNotAllowedException;
import com.clinic.booking.modules.appointment.exception.AppointmentInPastException;
import com.clinic.booking.modules.appointment.exception.AppointmentNotFoundException;
import com.clinic.booking.modules.appointment.exception.AppointmentSlotUnavailableException;
import com.clinic.booking.modules.appointment.exception.AppointmentStatusTargetNotAllowedException;
import com.clinic.booking.modules.appointment.exception.AppointmentStatusUpdateBeforeStartTimeException;
import com.clinic.booking.modules.appointment.exception.AppointmentStatusUpdateNotAllowedException;
import com.clinic.booking.modules.appointment.mapper.AppointmentMapper;
import com.clinic.booking.modules.appointment.repository.AppointmentRepository;
import com.clinic.booking.modules.availability.dto.response.AvailableSlotResponse;
import com.clinic.booking.modules.availability.service.AvailabilityService;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private AppointmentMapper appointmentMapper;

    private AppointmentServiceImpl appointmentService;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-03T02:00:00Z"),
            ZoneId.of("Asia/Ho_Chi_Minh"));

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentServiceImpl(
                appointmentMapper,
                appointmentRepository,
                doctorRepository,
                availabilityService,
                clock);
    }

    @Test
    void shouldCreateAppointmentWhenRequestedSlotIsAvailable() {
        Long doctorId = 3L;
        Long patientId = 10L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 4);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(8, 30);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                doctorId,
                patientId,
                appointmentDate,
                startTime);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        AvailableSlotResponse availableSlot = new AvailableSlotResponse(
                appointmentDate,
                startTime,
                endTime);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatientId(patientId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                1L,
                doctorId,
                patientId,
                appointmentDate,
                startTime,
                endTime,
                AppointmentStatus.BOOKED);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(availabilityService.getAvailableSlotsForDoctor(
                doctorId,
                appointmentDate))
                .thenReturn(List.of(availableSlot));

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndStartTimeAndStatus(
                        doctorId,
                        appointmentDate,
                        startTime,
                        AppointmentStatus.BOOKED))
                .thenReturn(false);

        when(appointmentMapper.toEntity(request, doctor, endTime))
                .thenReturn(appointment);

        when(appointmentRepository.saveAndFlush(appointment))
                .thenReturn(appointment);

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService
                .createAppointment(request);

        assertEquals(expectedResponse, actualResponse);

        verify(availabilityService).getAvailableSlotsForDoctor(
                doctorId,
                appointmentDate);

        verify(appointmentRepository).saveAndFlush(appointment);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentTimeIsInPast() {
        AppointmentCreateRequest request = new AppointmentCreateRequest(
                3L,
                10L,
                LocalDate.of(2026, 8, 2),
                LocalTime.of(8, 0));

        assertThrows(
                AppointmentInPastException.class,
                () -> appointmentService.createAppointment(request));

        verifyNoInteractions(
                appointmentRepository,
                doctorRepository,
                availabilityService,
                appointmentMapper);
    }

    @Test
    void shouldThrowExceptionWhenRequestedSlotIsUnavailable() {
        Long doctorId = 3L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 4);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                doctorId,
                10L,
                appointmentDate,
                LocalTime.of(9, 0));

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        AvailableSlotResponse availableSlot = new AvailableSlotResponse(
                appointmentDate,
                LocalTime.of(8, 0),
                LocalTime.of(8, 30));

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(availabilityService.getAvailableSlotsForDoctor(
                doctorId,
                appointmentDate))
                .thenReturn(List.of(availableSlot));

        assertThrows(
                AppointmentSlotUnavailableException.class,
                () -> appointmentService.createAppointment(request));

        verifyNoInteractions(
                appointmentRepository,
                appointmentMapper);
    }

    @Test
    void shouldThrowExceptionWhenRequestedSlotIsAlreadyBooked() {
        Long doctorId = 3L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 4);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(8, 30);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                doctorId,
                10L,
                appointmentDate,
                startTime);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        AvailableSlotResponse availableSlot = new AvailableSlotResponse(
                appointmentDate,
                startTime,
                endTime);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(availabilityService.getAvailableSlotsForDoctor(
                doctorId,
                appointmentDate))
                .thenReturn(List.of(availableSlot));

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndStartTimeAndStatus(
                        doctorId,
                        appointmentDate,
                        startTime,
                        AppointmentStatus.BOOKED))
                .thenReturn(true);

        assertThrows(
                AppointmentSlotUnavailableException.class,
                () -> appointmentService.createAppointment(request));

        verify(appointmentRepository, never()).saveAndFlush(any());
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldThrowExceptionWhenDoctorDoesNotExist() {
        Long doctorId = 999L;

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                doctorId,
                10L,
                LocalDate.of(2026, 8, 4),
                LocalTime.of(8, 0));

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> appointmentService.createAppointment(request));

        verifyNoInteractions(
                appointmentRepository,
                availabilityService,
                appointmentMapper);
    }

    @Test
    void shouldThrowSlotUnavailableExceptionWhenDatabaseRejectsDuplicateSlot() {
        Long doctorId = 3L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 4);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(8, 30);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                doctorId,
                10L,
                appointmentDate,
                startTime);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        AvailableSlotResponse availableSlot = new AvailableSlotResponse(
                appointmentDate,
                startTime,
                endTime);

        Appointment appointment = new Appointment();

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(availabilityService.getAvailableSlotsForDoctor(
                doctorId,
                appointmentDate))
                .thenReturn(List.of(availableSlot));

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndStartTimeAndStatus(
                        doctorId,
                        appointmentDate,
                        startTime,
                        AppointmentStatus.BOOKED))
                .thenReturn(false);

        when(appointmentMapper.toEntity(request, doctor, endTime))
                .thenReturn(appointment);

        when(appointmentRepository.saveAndFlush(appointment))
                .thenThrow(new DataIntegrityViolationException(
                        "Duplicate appointment slot"));

        assertThrows(
                AppointmentSlotUnavailableException.class,
                () -> appointmentService.createAppointment(request));

        verify(appointmentMapper, never()).toResponse(any());
    }

    @Test
    void shouldReturnAppointmentsForPatient() {
        Long patientId = 10L;

        Appointment firstAppointment = new Appointment();
        firstAppointment.setId(1L);
        firstAppointment.setPatientId(patientId);

        Appointment secondAppointment = new Appointment();
        secondAppointment.setId(2L);
        secondAppointment.setPatientId(patientId);

        AppointmentResponse firstResponse = new AppointmentResponse(
                1L,
                3L,
                patientId,
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                AppointmentStatus.BOOKED);

        AppointmentResponse secondResponse = new AppointmentResponse(
                2L,
                3L,
                patientId,
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                AppointmentStatus.BOOKED);

        when(appointmentRepository
                .findByPatientIdOrderByAppointmentDateAscStartTimeAsc(patientId))
                .thenReturn(List.of(firstAppointment, secondAppointment));

        when(appointmentMapper.toResponse(firstAppointment))
                .thenReturn(firstResponse);

        when(appointmentMapper.toResponse(secondAppointment))
                .thenReturn(secondResponse);

        List<AppointmentResponse> responses = appointmentService
                .getAppointmentsForPatient(patientId);

        assertEquals(
                List.of(firstResponse, secondResponse),
                responses);

        verify(appointmentRepository)
                .findByPatientIdOrderByAppointmentDateAscStartTimeAsc(patientId);
    }

    @Test
    void shouldCancelBookedAppointment() {
        Long appointmentId = 1L;
        Long doctorId = 3L;
        Long patientId = 10L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 4);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(8, 30);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setPatientId(patientId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                appointmentId,
                doctorId,
                patientId,
                appointmentDate,
                startTime,
                endTime,
                AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService
                .cancelAppointment(appointmentId);

        assertEquals(expectedResponse, actualResponse);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentDoesNotExistDuringCancellation() {
        Long appointmentId = 999L;

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.cancelAppointment(appointmentId));

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldThrowExceptionWhenCancellingAppointmentThatIsNotBooked() {
        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                AppointmentCancellationNotAllowedException.class,
                () -> appointmentService.cancelAppointment(appointmentId));

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldThrowExceptionWhenCancellingAppointmentAtOrAfterStartTime() {
        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentDate(LocalDate.of(2026, 8, 2));
        appointment.setStartTime(LocalTime.of(8, 0));
        appointment.setStatus(AppointmentStatus.BOOKED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                AppointmentCancellationDeadlinePassedException.class,
                () -> appointmentService.cancelAppointment(appointmentId));

        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldUpdateBookedAppointmentToCompleted() {
        Long appointmentId = 1L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 2);
        LocalTime startTime = LocalTime.of(8, 0);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.COMPLETED);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                appointmentId,
                3L,
                10L,
                appointmentDate,
                startTime,
                LocalTime.of(8, 30),
                AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService
                .updateAppointmentStatus(appointmentId, request);

        assertEquals(expectedResponse, actualResponse);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verify(appointmentMapper).toResponse(appointment);
    }

    @Test
    void shouldUpdateBookedAppointmentToNoShow() {
        Long appointmentId = 1L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 2);
        LocalTime startTime = LocalTime.of(8, 0);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.NO_SHOW);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                appointmentId,
                3L,
                10L,
                appointmentDate,
                startTime,
                LocalTime.of(8, 30),
                AppointmentStatus.NO_SHOW);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService
                .updateAppointmentStatus(appointmentId, request);

        assertEquals(expectedResponse, actualResponse);
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verify(appointmentMapper).toResponse(appointment);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentDoesNotExistDuringStatusUpdate() {
        Long appointmentId = 999L;

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.updateAppointmentStatus(appointmentId, request));

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = { "BOOKED", "CANCELLED" })
    void shouldThrowExceptionWhenRequestedTargetStatusIsNotCompletedOrNoShow(
            AppointmentStatus requestedStatus) {

        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.BOOKED);

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                requestedStatus);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                AppointmentStatusTargetNotAllowedException.class,
                () -> appointmentService.updateAppointmentStatus(appointmentId, request));

        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "BOOKED")
    void shouldThrowExceptionWhenAppointmentIsNotBookedDuringStatusUpdate(
            AppointmentStatus currentStatus) {

        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(currentStatus);

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                AppointmentStatusUpdateNotAllowedException.class,
                () -> appointmentService.updateAppointmentStatus(appointmentId, request));

        assertEquals(currentStatus, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingStatusBeforeAppointmentStartTime() {
        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentDate(LocalDate.of(2026, 8, 4));
        appointment.setStartTime(LocalTime.of(8, 0));
        appointment.setStatus(AppointmentStatus.BOOKED);

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                AppointmentStatusUpdateBeforeStartTimeException.class,
                () -> appointmentService.updateAppointmentStatus(appointmentId, request));

        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldUpdateAppointmentStatusAtAppointmentStartTime() {
        Long appointmentId = 1L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 3);
        LocalTime startTime = LocalTime.of(9, 0);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setStatus(AppointmentStatus.BOOKED);

        AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(
                AppointmentStatus.COMPLETED);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                appointmentId,
                3L,
                10L,
                appointmentDate,
                startTime,
                LocalTime.of(9, 30),
                AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService
                .updateAppointmentStatus(appointmentId, request);

        assertEquals(expectedResponse, actualResponse);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancellingAppointmentAtStartTime() {
        Long appointmentId = 1L;

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentDate(LocalDate.of(2026, 8, 3));
        appointment.setStartTime(LocalTime.of(9, 0));
        appointment.setStatus(AppointmentStatus.BOOKED);

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                AppointmentCancellationDeadlinePassedException.class,
                () -> appointmentService.cancelAppointment(appointmentId));

        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());

        verify(appointmentRepository).findById(appointmentId);
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void shouldCreateAppointmentAtCurrentTime() {
        Long doctorId = 3L;
        Long patientId = 10L;
        LocalDate appointmentDate = LocalDate.of(2026, 8, 3);
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(9, 30);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                doctorId,
                patientId,
                appointmentDate,
                startTime);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        AvailableSlotResponse availableSlot = new AvailableSlotResponse(
                appointmentDate,
                startTime,
                endTime);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatientId(patientId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        AppointmentResponse expectedResponse = new AppointmentResponse(
                1L,
                doctorId,
                patientId,
                appointmentDate,
                startTime,
                endTime,
                AppointmentStatus.BOOKED);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));
        when(availabilityService.getAvailableSlotsForDoctor(doctorId, appointmentDate))
                .thenReturn(List.of(availableSlot));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatus(
                doctorId,
                appointmentDate,
                startTime,
                AppointmentStatus.BOOKED))
                .thenReturn(false);
        when(appointmentMapper.toEntity(request, doctor, endTime))
                .thenReturn(appointment);
        when(appointmentRepository.saveAndFlush(appointment))
                .thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment))
                .thenReturn(expectedResponse);

        AppointmentResponse actualResponse = appointmentService.createAppointment(request);

        assertEquals(expectedResponse, actualResponse);
        verify(appointmentRepository).saveAndFlush(appointment);
    }
}
