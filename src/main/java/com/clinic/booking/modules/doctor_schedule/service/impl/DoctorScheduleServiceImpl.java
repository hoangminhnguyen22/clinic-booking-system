package com.clinic.booking.modules.doctor_schedule.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
import java.util.List;
import java.util.Objects;

import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleCreateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleUpdateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.response.DoctorScheduleResponse;
import com.clinic.booking.modules.doctor_schedule.exception.DoctorScheduleNotFoundException;
import com.clinic.booking.modules.doctor_schedule.exception.InvalidScheduleTimeException;
import com.clinic.booking.modules.doctor_schedule.exception.OverlappingDoctorScheduleException;
import com.clinic.booking.modules.doctor_schedule.service.DoctorScheduleService;
import com.clinic.booking.modules.doctor_schedule.mapper.DoctorScheduleMapper;
import com.clinic.booking.modules.doctor_schedule.repository.DoctorScheduleRepository;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;
import com.clinic.booking.modules.doctor_schedule.entity.DoctorSchedule;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorScheduleMapper doctorScheduleMapper;
    private final DoctorRepository doctorRepository;

    public DoctorScheduleServiceImpl(
            DoctorScheduleRepository doctorScheduleRepository,
            DoctorScheduleMapper doctorScheduleMapper,
            DoctorRepository doctorRepository) {
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorScheduleMapper = doctorScheduleMapper;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public DoctorScheduleResponse createSchedule(DoctorScheduleCreateRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new InvalidScheduleTimeException();
        }

        @SuppressWarnings("null")
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(request.doctorId()));

        if (doctorScheduleRepository.existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                request.doctorId(),
                request.dayOfWeek(),
                request.endTime(),
                request.startTime())) {
            throw new OverlappingDoctorScheduleException();
        }

        DoctorSchedule schedule = doctorScheduleMapper.toEntity(request, doctor);
        @SuppressWarnings("null")
        DoctorSchedule savedSchedule = doctorScheduleRepository.save(schedule);

        return doctorScheduleMapper.toResponse(savedSchedule);
    }

    @Override
    public List<DoctorScheduleResponse> getSchedulesByDoctorId(@NonNull Long doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorId(doctorId);
        return schedules.stream()
                .map(doctorScheduleMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteScheduleById(@NonNull Long scheduleId) {
        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new DoctorScheduleNotFoundException(scheduleId));
        doctorScheduleRepository.delete(Objects.requireNonNull(schedule));
    }

    @Override
    public DoctorScheduleResponse updateSchedule(Long scheduleId, DoctorScheduleUpdateRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new InvalidScheduleTimeException();
        }

        @SuppressWarnings("null")
        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new DoctorScheduleNotFoundException(scheduleId));

        if (doctorScheduleRepository.existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                schedule.getDoctor().getId(),
                request.dayOfWeek(),
                request.endTime(),
                request.startTime(),
                scheduleId)) {
            throw new OverlappingDoctorScheduleException();
        }

        doctorScheduleMapper.updateEntity(schedule, request);

        DoctorSchedule updatedSchedule = doctorScheduleRepository.save(schedule);
        return doctorScheduleMapper.toResponse(updatedSchedule);
    }
}
