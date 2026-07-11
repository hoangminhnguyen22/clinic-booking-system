package com.clinic.booking.modules.specialty.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.specialty.service.SpecialtyService;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {
    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public List<Specialty> getAll() {
        return specialtyService.getAll();
    }
}
