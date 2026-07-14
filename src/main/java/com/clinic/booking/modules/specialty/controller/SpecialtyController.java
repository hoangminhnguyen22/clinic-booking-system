package com.clinic.booking.modules.specialty.controller;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.booking.modules.specialty.dto.request.SpecialtyCreateRequest;
import com.clinic.booking.modules.specialty.dto.request.SpecialtyUpdateRequest;
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
    public ResponseEntity<SpecialtyResponse> getSpecialtyById(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(specialtyService.getSpecialtyById(id));
    }

    // PUT /api/specialties/1
    // ↓
    // SpecialtyController
    // ↓
    // Validation SpecialtyUpdateRequest
    // ↓
    // SpecialtyService.updateSpecialty()
    // ↓
    // SpecialtyServiceImpl
    // ↓
    // SpecialtyRepository
    // ↓
    // Database
    // ↓
    // SpecialtyResponse
    @PutMapping("/{id}")
    public SpecialtyResponse updateSpecialty(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody SpecialtyUpdateRequest request) {

        return specialtyService.updateSpecialty(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialty(@PathVariable @NonNull Long id) {
        specialtyService.deleteSpecialty(id);
    }
}
