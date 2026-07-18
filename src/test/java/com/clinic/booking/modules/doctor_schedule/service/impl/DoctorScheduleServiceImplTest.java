package com.clinic.booking.modules.doctor_schedule.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.List;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;
import com.clinic.booking.modules.doctor_schedule.dto.request.DoctorScheduleCreateRequest;
import com.clinic.booking.modules.doctor_schedule.dto.response.DoctorScheduleResponse;
import com.clinic.booking.modules.doctor_schedule.entity.DoctorSchedule;
import com.clinic.booking.modules.doctor_schedule.exception.DoctorScheduleNotFoundException;
import com.clinic.booking.modules.doctor_schedule.exception.InvalidScheduleTimeException;
import com.clinic.booking.modules.doctor_schedule.exception.OverlappingDoctorScheduleException;
import com.clinic.booking.modules.doctor_schedule.mapper.DoctorScheduleMapper;
import com.clinic.booking.modules.doctor_schedule.repository.DoctorScheduleRepository;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleMapper doctorScheduleMapper;

    @InjectMocks
    private DoctorScheduleServiceImpl doctorScheduleService;

    @Test
    void shouldThrowExceptionWhenStartTimeIsNotBeforeEndTime() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                1L,
                DayOfWeek.MONDAY,
                LocalTime.of(12, 0),
                LocalTime.of(8, 0));

        assertThrows(
                InvalidScheduleTimeException.class,
                () -> doctorScheduleService.createSchedule(request));

        verifyNoInteractions(
                doctorScheduleRepository,
                doctorRepository,
                doctorScheduleMapper);
    }

    @Test
    void shouldThrowExceptionWhenScheduleOverlaps() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                1L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0));

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(doctorScheduleRepository
                .existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                        1L,
                        DayOfWeek.MONDAY,
                        LocalTime.of(12, 0),
                        LocalTime.of(8, 0)))
                .thenReturn(true);

        assertThrows(
                OverlappingDoctorScheduleException.class,
                () -> doctorScheduleService.createSchedule(request));

        verify(doctorScheduleRepository)
                .existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                        1L,
                        DayOfWeek.MONDAY,
                        LocalTime.of(12, 0),
                        LocalTime.of(8, 0));

        verifyNoInteractions(doctorScheduleMapper);

        verify(doctorScheduleRepository, never())
                .save(any(DoctorSchedule.class));
    }

    @Test
    void shouldCreateScheduleWhenRequestIsValid() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                1L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0));

        // Arrange các mock tiếp theo ở đây
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(doctorScheduleRepository
                .existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                        1L,
                        DayOfWeek.MONDAY,
                        LocalTime.of(12, 0),
                        LocalTime.of(8, 0)))
                .thenReturn(false);

        DoctorSchedule schedule = new DoctorSchedule();

        DoctorSchedule savedSchedule = new DoctorSchedule();
        savedSchedule.setId(10L);
        savedSchedule.setDoctor(doctor);
        savedSchedule.setDayOfWeek(DayOfWeek.MONDAY);
        savedSchedule.setStartTime(LocalTime.of(8, 0));
        savedSchedule.setEndTime(LocalTime.of(12, 0));

        DoctorScheduleResponse expectedResponse = new DoctorScheduleResponse(
                10L,
                1L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0));

        when(doctorScheduleMapper.toEntity(request, doctor))
                .thenReturn(schedule);

        when(doctorScheduleRepository.save(schedule))
                .thenReturn(savedSchedule);

        when(doctorScheduleMapper.toResponse(savedSchedule))
                .thenReturn(expectedResponse);

        // Act gọi createSchedule(...)
        // Assert response và verify save(...)
        DoctorScheduleResponse actualResponse = doctorScheduleService.createSchedule(request);

        assertEquals(expectedResponse, actualResponse);

        verify(doctorScheduleRepository).save(schedule);
    }

    @Test
    void shouldReturnSchedulesWhenDoctorExists() {
        // Arrange (chuẩn bị dữ liệu và hành vi giả của dependency)
        // Mock = object giả do Mockito tạo, không gọi database thật.
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(10L);
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(12, 0));

        DoctorScheduleResponse expectedResponse = new DoctorScheduleResponse(
                10L,
                1L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0));

        // 1. Giả lập doctor ID 1 tồn tại.
        // Optional.of(doctor) nghĩa là repository tìm thấy doctor.
        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        // 2. Giả lập database trả một danh sách gồm schedule này.
        // List.of(...) tạo list chỉ đọc, phù hợp làm dữ liệu test.
        when(doctorScheduleRepository.findByDoctorId(1L))
                .thenReturn(List.of(schedule));

        // 3. Giả lập mapper chuyển entity schedule thành response API.
        when(doctorScheduleMapper.toResponse(schedule))
                .thenReturn(expectedResponse);

        // Act (gọi method thật đang cần test).
        List<DoctorScheduleResponse> actualResponses = doctorScheduleService.getSchedulesByDoctorId(1L);

        // Assert (kiểm tra kết quả thực tế bằng kết quả mong đợi).
        assertEquals(List.of(expectedResponse), actualResponses);

        // Verify (xác nhận service đã gọi đúng dependency).
        verify(doctorScheduleRepository).findByDoctorId(1L);
        verify(doctorScheduleMapper).toResponse(schedule);
    }

    @Test
    void shouldDeleteScheduleWhenScheduleExists() {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(10L);

        when(doctorScheduleRepository.findById(10L))
                .thenReturn(Optional.of(schedule));

        doctorScheduleService.deleteScheduleById(10L);

        verify(doctorScheduleRepository).delete(schedule);
    }

    @Test
    void shouldThrowExceptionWhenScheduleToDeleteDoesNotExist() {
        when(doctorScheduleRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorScheduleNotFoundException.class,
                () -> doctorScheduleService.deleteScheduleById(10L));

        verify(doctorScheduleRepository, never())
                .delete(any(DoctorSchedule.class));
    }
}