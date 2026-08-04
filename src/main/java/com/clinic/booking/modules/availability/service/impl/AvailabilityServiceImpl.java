package com.clinic.booking.modules.availability.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Clock;

import org.springframework.stereotype.Service;

import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.appointment.repository.AppointmentRepository;
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
    private final AppointmentRepository appointmentRepository;
    private final Clock clock;

    public AvailabilityServiceImpl(
            DoctorRepository doctorRepository,
            DoctorScheduleRepository doctorScheduleRepository,
            AppointmentRepository appointmentRepository,
            Clock clock) {

        this.doctorRepository = doctorRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.clock = clock;
    }

    @Override
    public List<AvailableSlotResponse> getAvailableSlotsForDoctor(
            Long doctorId,
            LocalDate date) {

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        if (date.isBefore(today)) {
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

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndStatus(
                        doctorId,
                        date,
                        AppointmentStatus.BOOKED);

        Set<LocalTime> bookedStartTimes = new HashSet<>();

        for (Appointment bookedAppointment : bookedAppointments) {
            bookedStartTimes.add(bookedAppointment.getStartTime());
        }

        List<AvailableSlotResponse> availableSlots = new ArrayList<>();
        int durationMinutes = doctor.getAppointmentDurationMinutes();

        for (DoctorSchedule schedule : schedules) {
            LocalTime currentStart = schedule.getStartTime();
            LocalTime candidateEnd = currentStart.plusMinutes(durationMinutes);

            while (!candidateEnd.isAfter(schedule.getEndTime())) {
                boolean hasNotStartedYet = date.isAfter(today) || !currentStart.isBefore(currentTime);

                if (hasNotStartedYet && !bookedStartTimes.contains(currentStart)) {
                    availableSlots.add(new AvailableSlotResponse(
                            date,
                            currentStart,
                            candidateEnd));
                }

                currentStart = candidateEnd;
                candidateEnd = currentStart.plusMinutes(durationMinutes);
            }
        }

        return availableSlots;
    }
}