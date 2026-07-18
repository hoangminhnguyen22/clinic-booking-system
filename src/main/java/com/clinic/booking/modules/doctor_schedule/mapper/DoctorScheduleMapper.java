package com.clinic.booking.modules.doctor_schedule.mapper;

import org.springframework.stereotype.Component;

import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleCreateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleUpdateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.response.DoctorScheduleResponse;
import com.clinic.booking.modules.doctor_schedule.entity.DoctorSchedule;

@Component
public class DoctorScheduleMapper {

    public DoctorSchedule toEntity(DoctorScheduleCreateRequest request, Doctor doctor) {
        DoctorSchedule doctorSchedule = new DoctorSchedule();
        doctorSchedule.setDoctor(doctor);
        doctorSchedule.setStartTime(request.startTime());
        doctorSchedule.setEndTime(request.endTime());
        doctorSchedule.setDayOfWeek(request.dayOfWeek());
        return doctorSchedule;
    }

    public DoctorScheduleResponse toResponse(DoctorSchedule doctorSchedule) {
        return new DoctorScheduleResponse(
                doctorSchedule.getId(),
                doctorSchedule.getDoctor().getId(),
                doctorSchedule.getDayOfWeek(),
                doctorSchedule.getStartTime(),
                doctorSchedule.getEndTime());
    }

    public void updateEntity(DoctorSchedule doctorSchedule, DoctorScheduleUpdateRequest request) {
        doctorSchedule.setStartTime(request.startTime());
        doctorSchedule.setEndTime(request.endTime());
        doctorSchedule.setDayOfWeek(request.dayOfWeek());
    }
}
