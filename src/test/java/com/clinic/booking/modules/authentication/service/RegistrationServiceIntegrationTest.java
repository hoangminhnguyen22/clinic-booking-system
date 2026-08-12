package com.clinic.booking.modules.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.booking.modules.authentication.dto.request.RegistrationRequest;
import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;
import com.clinic.booking.modules.authentication.exception.RegistrationEmailAlreadyExistsException;
import com.clinic.booking.modules.patient.entity.PatientProfile;
import com.clinic.booking.modules.patient.repository.PatientProfileRepository;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegistrationServiceIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterPatientWithCanonicalEmailAndArgon2PasswordHash() {
        String uniquePart = UUID.randomUUID().toString();
        String suppliedEmail = "  Registration-" + uniquePart + "@Example.COM  ";
        String canonicalEmail = suppliedEmail
                .trim()
                .toLowerCase(Locale.ROOT);
        String rawPassword = "example-credential";

        RegistrationRequest request = new RegistrationRequest(suppliedEmail, rawPassword);

        RegistrationResponse response = registrationService.register(request);

        assertNotNull(response.id());
        assertEquals(canonicalEmail, response.email());

        User savedUser = userRepository.findByEmail(canonicalEmail)
                .orElseThrow();

        assertEquals(response.id(), savedUser.getId());
        assertEquals(canonicalEmail, savedUser.getEmail());
        assertFalse(savedUser.getPasswordHash().equals(rawPassword));
        assertTrue(passwordEncoder.matches(
                rawPassword,
                savedUser.getPasswordHash()));
        assertTrue(savedUser.isEnabled());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(Role.PATIENT));

        PatientProfile patientProfile = patientProfileRepository.findByUserId(savedUser.getId())
                .orElseThrow();

        assertNotNull(patientProfile.getId());
        assertEquals(
                savedUser.getId(),
                patientProfile.getUser().getId());
    }

    @Test
    void shouldRejectDuplicateCanonicalEmailWithoutCreatingAnotherProfile() {
        String uniquePart = UUID.randomUUID().toString();
        String canonicalEmail = "duplicate-" + uniquePart + "@example.com";

        RegistrationResponse firstResponse = registrationService.register(new RegistrationRequest(
                canonicalEmail,
                "first-example-credential"));

        RegistrationEmailAlreadyExistsException exception = assertThrows(
                RegistrationEmailAlreadyExistsException.class,
                () -> registrationService.register(new RegistrationRequest(
                        "  " + canonicalEmail.toUpperCase(Locale.ROOT) + "  ",
                        "second-example-credential")));

        assertEquals(
                "Unable to create account with the supplied details.",
                exception.getMessage());

        User savedUser = userRepository.findByEmail(canonicalEmail)
                .orElseThrow();

        assertEquals(firstResponse.id(), savedUser.getId());
        assertTrue(patientProfileRepository
                .findByUserId(savedUser.getId())
                .isPresent());
    }
}