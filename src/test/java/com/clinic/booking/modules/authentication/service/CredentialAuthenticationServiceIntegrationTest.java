package com.clinic.booking.modules.authentication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
class CredentialAuthenticationServiceIntegrationTest {

    @Autowired
    private CredentialAuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldAuthenticatePersistedUserWithCurrentLazyLoadedRoles() {
        String canonicalEmail = "authentication-" + UUID.randomUUID() + "@example.com";
        String rawPassword = "example-credential";

        User user = new User();
        user.setEmail(canonicalEmail);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRoles(Set.of(Role.PATIENT, Role.DOCTOR));

        User savedUser = userRepository.saveAndFlush(user);

        assertNotNull(savedUser.getId());

        try {
            Optional<AuthenticatedActor> result = authenticationService.authenticate(
                    "  " + canonicalEmail.toUpperCase(Locale.ROOT) + "  ",
                    rawPassword);

            assertTrue(result.isPresent());

            AuthenticatedActor actor = result.orElseThrow();

            assertEquals(savedUser.getId(), actor.userId());
            assertEquals(
                    Set.of(Role.PATIENT, Role.DOCTOR),
                    actor.roles());
        } finally {
            userRepository.deleteById(savedUser.getId());
        }
    }
}