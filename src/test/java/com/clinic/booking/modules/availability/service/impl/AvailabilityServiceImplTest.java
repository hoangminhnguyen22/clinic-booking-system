package com.clinic.booking.modules.availability.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinic.booking.modules.appointment.entity.Appointment;
import com.clinic.booking.modules.appointment.entity.AppointmentStatus;
import com.clinic.booking.modules.appointment.repository.AppointmentRepository;
import com.clinic.booking.modules.availability.dto.response.AvailableSlotResponse;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;
import com.clinic.booking.modules.doctor_schedule.entity.DoctorSchedule;
import com.clinic.booking.modules.doctor_schedule.repository.DoctorScheduleRepository;
import com.clinic.booking.modules.availability.exception.PastDateAvailabilityException;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Test
    void shouldGenerateSlotsForDoctorSchedule() {
        Long doctorId = 3L;
        LocalDate date = LocalDate.now().plusDays(1);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setActive(true);
        doctor.setAppointmentDurationMinutes(30);

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(date.getDayOfWeek());
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(12, 0));

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(
                doctorId,
                date.getDayOfWeek()))
                .thenReturn(List.of(schedule));

        List<AvailableSlotResponse> slots = availabilityService.getAvailableSlotsForDoctor(
                doctorId,
                date);

        assertEquals(8, slots.size());

        assertEquals(
                new AvailableSlotResponse(
                        date,
                        LocalTime.of(8, 0),
                        LocalTime.of(8, 30)),
                slots.getFirst());

        assertEquals(
                new AvailableSlotResponse(
                        date,
                        LocalTime.of(11, 30),
                        LocalTime.of(12, 0)),
                slots.getLast());
    }

    @Test
    void shouldReturnNoSlotsWhenDoctorIsInactive() {
        Long doctorId = 3L;
        LocalDate date = LocalDate.now().plusDays(1);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setActive(false);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        List<AvailableSlotResponse> slots = availabilityService.getAvailableSlotsForDoctor(
                doctorId,
                date);

        assertTrue(slots.isEmpty());

        verifyNoInteractions(
                doctorScheduleRepository,
                appointmentRepository);
    }

    @Test
    void shouldThrowExceptionWhenAvailabilityDateIsInThePast() {
        Long doctorId = 3L;
        LocalDate pastDate = LocalDate.now().minusDays(1);

        assertThrows(
                PastDateAvailabilityException.class,
                () -> availabilityService.getAvailableSlotsForDoctor(
                        doctorId,
                        pastDate));

        verifyNoInteractions(
                doctorRepository,
                doctorScheduleRepository,
                appointmentRepository);
    }

    @Test
    void shouldExcludeBookedSlotFromAvailableSlots() {
        Long doctorId = 3L;
        LocalDate date = LocalDate.now().plusDays(1);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setActive(true);
        doctor.setAppointmentDurationMinutes(30);

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(date.getDayOfWeek());
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(10, 0));

        Appointment bookedAppointment = new Appointment();
        bookedAppointment.setDoctor(doctor);
        bookedAppointment.setAppointmentDate(date);
        bookedAppointment.setStartTime(LocalTime.of(8, 30));
        bookedAppointment.setEndTime(LocalTime.of(9, 0));
        bookedAppointment.setStatus(AppointmentStatus.BOOKED);

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(
                doctorId,
                date.getDayOfWeek()))
                .thenReturn(List.of(schedule));

        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndStatus(
                        doctorId,
                        date,
                        AppointmentStatus.BOOKED))
                .thenReturn(List.of(bookedAppointment));

        List<AvailableSlotResponse> slots = availabilityService
                .getAvailableSlotsForDoctor(doctorId, date);

        assertEquals(3, slots.size());

        assertFalse(slots.stream()
                .anyMatch(slot -> slot.startTime()
                        .equals(LocalTime.of(8, 30))));
    }
}
