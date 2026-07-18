package com.clinic.booking.modules.availability.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.booking.modules.availability.dto.response.AvailableSlotResponse;
import com.clinic.booking.modules.availability.service.AvailabilityService;

@RestController
@RequestMapping("/api/doctors")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{doctorId}/available-slots")
    public List<AvailableSlotResponse> getAvailableSlots(
            @PathVariable("doctorId") Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return availabilityService.getAvailableSlotsForDoctor(doctorId, date);
    }
}
