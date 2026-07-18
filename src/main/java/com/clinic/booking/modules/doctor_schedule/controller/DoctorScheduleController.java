package com.clinic.booking.modules.doctor_schedule.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.lang.NonNull;
import com.clinic.booking.modules.doctor_schedule.service.DoctorScheduleService;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleCreateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleUpdateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.response.DoctorScheduleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-schedules")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorScheduleResponse createDoctorSchedule(@Valid @RequestBody DoctorScheduleCreateRequest request) {
        return doctorScheduleService.createSchedule(request);
    }

    @GetMapping
    public List<DoctorScheduleResponse> getSchedulesByDoctorId(@RequestParam("doctorId") @NonNull Long doctorId) {
        return doctorScheduleService.getSchedulesByDoctorId(doctorId);
    }

    @PutMapping("/{scheduleId}")
    public DoctorScheduleResponse updateDoctorSchedule(@PathVariable("scheduleId") Long scheduleId,
            @Valid @RequestBody DoctorScheduleUpdateRequest request) {
        return doctorScheduleService.updateSchedule(scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScheduleById(@PathVariable("scheduleId") @NonNull Long scheduleId) {
        doctorScheduleService.deleteScheduleById(scheduleId);
    }
}
