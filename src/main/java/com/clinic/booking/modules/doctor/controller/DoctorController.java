package com.clinic.booking.modules.doctor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.booking.modules.doctor.dto.request.DoctorCreateRequest;
import com.clinic.booking.modules.doctor.dto.request.DoctorUpdateRequest;
import com.clinic.booking.modules.doctor.dto.response.DoctorResponse;
import com.clinic.booking.modules.doctor.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponse createDoctor(
            @Valid @RequestBody DoctorCreateRequest request) {

        return doctorService.createDoctor(request);
    }

    @GetMapping
    public List<DoctorResponse> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public DoctorResponse getDoctorById(
            @PathVariable("id") Long id) {

        return doctorService.getDoctorById(id);
    }

    @PutMapping("/{id}")
    public DoctorResponse updateDoctor(
            @PathVariable("id") Long id,
            @Valid @RequestBody DoctorUpdateRequest request) {

        return doctorService.updateDoctor(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(
            @PathVariable("id") Long id) {

        doctorService.deleteDoctor(id);
    }
}