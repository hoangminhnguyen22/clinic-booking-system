package com.clinic.booking.modules.specialty.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.booking.modules.specialty.dto.request.SpecialtyCreateRequest;
import com.clinic.booking.modules.specialty.dto.response.SpecialtyResponse;
import com.clinic.booking.modules.specialty.service.SpecialtyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {
    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public List<SpecialtyResponse> getAll() {
        return specialtyService.getAll();
    }

    /*
     * POST JSON
     * → SpecialtyCreateRequest
     * → SpecialtyController
     * → SpecialtyService
     * → SpecialtyMapper
     * → Specialty Entity
     * → Repository save
     * → SpecialtyResponse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialtyResponse createSpecialty(
            @Valid @RequestBody SpecialtyCreateRequest request) {
        return specialtyService.createSpecialty(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponse> getSpecialtyById(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyService.getSpecialtyById(id));
    }
}
