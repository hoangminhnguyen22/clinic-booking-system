package com.clinic.booking.modules.doctor_schedule.repository;

import java.util.List;

import com.clinic.booking.modules.doctor_schedule.entity.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    // Check if a schedule exists for a doctor on a specific day of the week that
    // overlaps with the given time range
    boolean existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime endTime,
            LocalTime startTime);

    boolean existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime endTime,
            LocalTime startTime,
            Long scheduleId);

    List<DoctorSchedule> findByDoctorId(Long doctorId);

    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(
            Long doctorId,
            DayOfWeek dayOfWeek);
}
