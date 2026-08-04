package com.clinic.booking.modules.appointment.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

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
import com.clinic.booking.modules.appointment.service.AppointmentService;
import com.clinic.booking.modules.availability.dto.response.AvailableSlotResponse;
import com.clinic.booking.modules.availability.service.AvailabilityService;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilityService availabilityService;
    private final Clock clock;

    public AppointmentServiceImpl(
            AppointmentMapper appointmentMapper,
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            AvailabilityService availabilityService,
            Clock clock) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityService = availabilityService;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(
                request.appointmentDate(),
                request.startTime());

        if (appointmentDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new AppointmentInPastException();
        }

        @SuppressWarnings("null")
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(request.doctorId()));

        AvailableSlotResponse availableSlot = availabilityService
                .getAvailableSlotsForDoctor(
                        request.doctorId(),
                        request.appointmentDate())
                .stream()
                .filter(slot -> slot.startTime().equals(request.startTime()))
                .findFirst()
                .orElseThrow(AppointmentSlotUnavailableException::new);

        boolean alreadyBooked = appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndStartTimeAndStatus(
                        request.doctorId(),
                        request.appointmentDate(),
                        request.startTime(),
                        AppointmentStatus.BOOKED);

        if (alreadyBooked) {
            throw new AppointmentSlotUnavailableException();
        }

        Appointment appointment = appointmentMapper.toEntity(
                request,
                doctor,
                availableSlot.endTime());

        // Với Appointment, double-booking là rule quan trọng nên cần thêm lớp fallback
        // này. nếu database báo lỗi integrity lúc lưu, đổi từ lỗi kỹ thuật sang lỗi
        // nghiệp vụ AppointmentSlotUnavailableException
        try {
            @SuppressWarnings("null")
            Appointment savedAppointment = appointmentRepository.saveAndFlush(appointment);

            return appointmentMapper.toResponse(savedAppointment);
        } catch (DataIntegrityViolationException exception) {
            throw new AppointmentSlotUnavailableException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateAscStartTimeAsc(patientId)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId) {
        @SuppressWarnings("null")
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new AppointmentCancellationNotAllowedException();
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getStartTime());

        if (!appointmentDateTime.isAfter(LocalDateTime.now(clock))) {
            throw new AppointmentCancellationDeadlinePassedException();
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatusUpdateRequest request) {
        @SuppressWarnings("null")
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        if (request.status() != AppointmentStatus.COMPLETED && request.status() != AppointmentStatus.NO_SHOW) {
            throw new AppointmentStatusTargetNotAllowedException();
        }

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new AppointmentStatusUpdateNotAllowedException();
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getStartTime());

        if (appointmentDateTime.isAfter(LocalDateTime.now(clock))) {
            throw new AppointmentStatusUpdateBeforeStartTimeException();
        }

        appointment.setStatus(request.status());

        return appointmentMapper.toResponse(appointment);
    }
}
