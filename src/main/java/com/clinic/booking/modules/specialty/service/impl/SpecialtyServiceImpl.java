package com.clinic.booking.modules.specialty.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.booking.modules.specialty.dto.request.SpecialtyCreateRequest;
import com.clinic.booking.modules.specialty.dto.request.SpecialtyUpdateRequest;
import com.clinic.booking.modules.specialty.dto.response.SpecialtyResponse;
import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.specialty.exception.SpecialtyAlreadyExistsException;
import com.clinic.booking.modules.specialty.exception.SpecialtyNotFoundException;
import com.clinic.booking.modules.specialty.mapper.SpecialtyMapper;
import com.clinic.booking.modules.specialty.repository.SpecialtyRepository;
import com.clinic.booking.modules.specialty.service.SpecialtyService;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public List<SpecialtyResponse> getAll() {
        return specialtyRepository.findAll()
                .stream()
                .map(specialtyMapper::toResponse)
                .toList();
    }

    /*
     * SpecialtyCreateRequest
     * → mapper.toEntity()
     * → repository.save()
     * → database sinh id
     * → mapper.toResponse()
     * → SpecialtyResponse
     * Nhận name
     * → kiểm tra tên đã tồn tại
     * → nếu có: ném SpecialtyAlreadyExistsException
     * → nếu chưa: lưu vào database
     */
    @Override
    public SpecialtyResponse createSpecialty(SpecialtyCreateRequest request) {
        if (specialtyRepository.existsByNameIgnoreCase(request.name())) {
            throw new SpecialtyAlreadyExistsException(request.name());
        }

        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty savedSpecialty = specialtyRepository.save(specialty);

        return specialtyMapper.toResponse(savedSpecialty);
    }

    @Override
    public SpecialtyResponse getSpecialtyById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException(id));

        return specialtyMapper.toResponse(specialty);
    }

    /*
     * Nhận id và SpecialtyUpdateRequest
     * ↓
     * Tìm Specialty theo id
     * ↓
     * Không tồn tại → SpecialtyNotFoundException
     * ↓
     * Trim tên mới
     * ↓
     * Kiểm tra tên có thay đổi hay không
     * ↓
     * Nếu thay đổi, kiểm tra duplicate
     * ↓
     * Trùng tên → SpecialtyAlreadyExistsException
     * ↓
     * Gán tên mới vào entity
     * ↓
     * Repository.save()
     * ↓
     * Mapper chuyển Entity thành SpecialtyResponse
     * ↓
     * Trả kết quả
     */
    @Override
    public SpecialtyResponse updateSpecialty(Long id, SpecialtyUpdateRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException(id));

        String normalizedName = request.name().trim();

        boolean nameChanged = !specialty.getName().equalsIgnoreCase(normalizedName);

        if (nameChanged && specialtyRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new SpecialtyAlreadyExistsException(normalizedName);
        }

        specialty.setName(normalizedName);

        Specialty updatedSpecialty = specialtyRepository.save(specialty);

        return specialtyMapper.toResponse(updatedSpecialty);
    }
}
