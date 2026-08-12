package com.clinic.booking.modules.authentication.service.impl;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinic.booking.modules.authentication.dto.request.RegistrationRequest;
import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;
import com.clinic.booking.modules.authentication.exception.RegistrationEmailAlreadyExistsException;
import com.clinic.booking.modules.authentication.mapper.RegistrationMapper;
import com.clinic.booking.modules.authentication.service.RegistrationService;
import com.clinic.booking.modules.patient.repository.PatientProfileRepository;
import com.clinic.booking.modules.user.repository.UserRepository;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.patient.entity.PatientProfile;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationMapper registrationMapper;

    public RegistrationServiceImpl(
            UserRepository userRepository,
            PatientProfileRepository patientProfileRepository,
            PasswordEncoder passwordEncoder,
            RegistrationMapper registrationMapper) {
        this.userRepository = userRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrationMapper = registrationMapper;
    }

    @Override
    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RegistrationEmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordHash);
        user.getRoles().add(Role.PATIENT);

        User savedUser;
        try {
            savedUser = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new RegistrationEmailAlreadyExistsException();
        }

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(savedUser);

        patientProfileRepository.save(patientProfile);

        return registrationMapper.toResponse(savedUser);
    }
}