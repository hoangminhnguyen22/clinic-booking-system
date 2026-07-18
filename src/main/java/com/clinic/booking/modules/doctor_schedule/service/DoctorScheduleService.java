package com.clinic.booking.modules.doctor_schedule.service;

import java.util.List;

import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleCreateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleUpdateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.response.DoctorScheduleResponse;

import org.springframework.lang.NonNull;

public interface DoctorScheduleService {

    DoctorScheduleResponse createSchedule(DoctorScheduleCreateRequest request);

    List<DoctorScheduleResponse> getSchedulesByDoctorId(@NonNull Long doctorId);

    DoctorScheduleResponse updateSchedule(Long scheduleId, DoctorScheduleUpdateRequest request);

    void deleteScheduleById(@NonNull Long scheduleId);
}
