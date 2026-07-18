package com.clinic.booking.modules.availability.service;

import java.time.LocalDate;
import java.util.List;

import com.clinic.booking.modules.availability.dto.response.AvailableSlotResponse;

public interface AvailabilityService {

    List<AvailableSlotResponse> getAvailableSlotsForDoctor(Long doctorId, LocalDate date);
}
