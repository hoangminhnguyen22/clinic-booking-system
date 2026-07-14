package com.clinic.booking.modules.doctor.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.booking.modules.doctor.dto.request.DoctorCreateRequest;
import com.clinic.booking.modules.doctor.dto.request.DoctorUpdateRequest;
import com.clinic.booking.modules.doctor.dto.response.DoctorResponse;
import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.doctor.exception.DoctorNotFoundException;
import com.clinic.booking.modules.doctor.exception.DuplicateDoctorEmailException;
import com.clinic.booking.modules.doctor.mapper.DoctorMapper;
import com.clinic.booking.modules.doctor.repository.DoctorRepository;
import com.clinic.booking.modules.doctor.service.DoctorService;
import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.specialty.exception.SpecialtyNotFoundException;
import com.clinic.booking.modules.specialty.repository.SpecialtyRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final SpecialtyRepository specialtyRepository;

    public DoctorServiceImpl(
            DoctorRepository doctorRepository,
            DoctorMapper doctorMapper,
            SpecialtyRepository specialtyRepository) {

        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public DoctorResponse createDoctor(DoctorCreateRequest request) {
        if (doctorRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateDoctorEmailException(request.email());
        }

        @SuppressWarnings("null")
        Specialty specialty = specialtyRepository
                .findById(request.specialtyId())
                .orElseThrow(() -> new SpecialtyNotFoundException(request.specialtyId()));

        Doctor doctor = doctorMapper.toEntity(request, specialty);
        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toResponse(savedDoctor);
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .toList();
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        @SuppressWarnings("null")
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));

        return doctorMapper.toResponse(doctor);
    }

    @Override
    public DoctorResponse updateDoctor(
            Long id,
            DoctorUpdateRequest request) {

        @SuppressWarnings("null")
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));

        if (doctorRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new DuplicateDoctorEmailException(request.email());
        }

        @SuppressWarnings("null")
        Specialty specialty = specialtyRepository
                .findById(request.specialtyId())
                .orElseThrow(() -> new SpecialtyNotFoundException(request.specialtyId()));

        doctorMapper.updateEntity(doctor, request, specialty);

        @SuppressWarnings("null")
        Doctor updatedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toResponse(updatedDoctor);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));

        doctorRepository.delete(doctor);
    }
}
