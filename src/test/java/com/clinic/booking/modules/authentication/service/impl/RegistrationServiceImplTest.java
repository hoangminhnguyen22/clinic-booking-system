package com.clinic.booking.modules.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clinic.booking.modules.authentication.dto.request.RegistrationRequest;
import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;
import com.clinic.booking.modules.authentication.exception.RegistrationEmailAlreadyExistsException;
import com.clinic.booking.modules.authentication.mapper.RegistrationMapper;
import com.clinic.booking.modules.patient.entity.PatientProfile;
import com.clinic.booking.modules.patient.repository.PatientProfileRepository;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientProfileRepository patientProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RegistrationMapper registrationMapper;

    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl(
                userRepository,
                patientProfileRepository,
                passwordEncoder,
                registrationMapper);
    }

    @Test
    void shouldNormalizeEmailAndRejectDuplicateBeforeFurtherProcessing() {
        RegistrationRequest request = new RegistrationRequest(
                "  Patient@Example.COM  ",
                "example-credential");

        when(userRepository.existsByEmail("patient@example.com"))
                .thenReturn(true);

        RegistrationEmailAlreadyExistsException exception = assertThrows(
                RegistrationEmailAlreadyExistsException.class,
                () -> registrationService.register(request));

        assertEquals(
                "Unable to create account with the supplied details.",
                exception.getMessage());

        verify(userRepository).existsByEmail("patient@example.com");
        verifyNoInteractions(
                patientProfileRepository,
                passwordEncoder,
                registrationMapper);
    }

    @Test
    void shouldCreatePatientUserAndLinkedPatientProfile() {
        String rawPassword = " example-credential ";
        String passwordHash = "fake-password-hash";

        RegistrationRequest request = new RegistrationRequest(
                "  Patient@Example.COM  ",
                rawPassword);

        User savedUser = new User();
        savedUser.setId(42L);
        savedUser.setEmail("patient@example.com");
        savedUser.setPasswordHash(passwordHash);
        savedUser.getRoles().add(Role.PATIENT);

        RegistrationResponse expectedResponse = new RegistrationResponse(42L, "patient@example.com");

        when(userRepository.existsByEmail("patient@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode(rawPassword))
                .thenReturn(passwordHash);
        when(userRepository.saveAndFlush(any(User.class)))
                .thenReturn(savedUser);
        when(registrationMapper.toResponse(savedUser))
                .thenReturn(expectedResponse);

        RegistrationResponse response = registrationService.register(request);

        assertSame(expectedResponse, response);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<PatientProfile> profileCaptor = ArgumentCaptor.forClass(PatientProfile.class);

        verify(userRepository).existsByEmail("patient@example.com");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        verify(patientProfileRepository).save(profileCaptor.capture());
        verify(registrationMapper).toResponse(savedUser);

        User userBeforeSave = userCaptor.getValue();

        assertEquals("patient@example.com", userBeforeSave.getEmail());
        assertEquals(passwordHash, userBeforeSave.getPasswordHash());
        assertFalse(userBeforeSave.getPasswordHash().equals(rawPassword));
        assertEquals(1, userBeforeSave.getRoles().size());
        assertTrue(userBeforeSave.getRoles().contains(Role.PATIENT));
        assertTrue(userBeforeSave.isEnabled());
        assertNull(userBeforeSave.getId());

        PatientProfile patientProfile = profileCaptor.getValue();

        assertSame(savedUser, patientProfile.getUser());
        assertEquals(42L, patientProfile.getUser().getId());
    }

    @Test
    void shouldMapEmailUniqueConstraintRaceToRegistrationConflict() {
        String rawPassword = "example-credential";
        RegistrationRequest request = new RegistrationRequest(
                "Patient@Example.COM",
                rawPassword);

        when(userRepository.existsByEmail("patient@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode(rawPassword))
                .thenReturn("fake-password-hash");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "simulated unique constraint violation"));

        RegistrationEmailAlreadyExistsException exception = assertThrows(
                RegistrationEmailAlreadyExistsException.class,
                () -> registrationService.register(request));

        assertEquals(
                "Unable to create account with the supplied details.",
                exception.getMessage());

        verify(userRepository).existsByEmail("patient@example.com");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).saveAndFlush(any(User.class));
        verifyNoInteractions(
                patientProfileRepository,
                registrationMapper);
    }
}