package com.clinic.booking.modules.availability.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.booking.modules.availability.dto.response.AvailableSlotResponse;
import com.clinic.booking.modules.availability.service.AvailabilityService;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;
import com.clinic.booking.modules.doctor_schedule.entity.DoctorSchedule;
import com.clinic.booking.modules.doctor_schedule.repository.DoctorScheduleRepository;
import com.clinic.booking.modules.availability.exception.PastDateAvailabilityException;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public AvailabilityServiceImpl(
            DoctorRepository doctorRepository,
            DoctorScheduleRepository doctorScheduleRepository) {

        this.doctorRepository = doctorRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    @Override
    public List<AvailableSlotResponse> getAvailableSlotsForDoctor(
            Long doctorId,
            LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new PastDateAvailabilityException();
        }

        @SuppressWarnings("null")
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        if (!doctor.isActive()) {
            return List.of();
        }

        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek());

        List<AvailableSlotResponse> availableSlots = new ArrayList<>();
        int durationMinutes = doctor.getAppointmentDurationMinutes();

        for (DoctorSchedule schedule : schedules) {
            LocalTime currentStart = schedule.getStartTime();
            LocalTime candidateEnd = currentStart.plusMinutes(durationMinutes);

            while (!candidateEnd.isAfter(schedule.getEndTime())) {
                availableSlots.add(new AvailableSlotResponse(
                        date,
                        currentStart,
                        candidateEnd));

                currentStart = candidateEnd;
                candidateEnd = currentStart.plusMinutes(durationMinutes);
            }
        }

        return availableSlots;
    }
}