package com.clinic.booking.modules.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CredentialAuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CredentialAuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new CredentialAuthenticationServiceImpl(
                userRepository,
                passwordEncoder);
    }

    @Test
    void shouldAuthenticateEnabledUserWithMatchingPasswordAndCurrentRoles() {
        String rawPassword = " example-credential ";

        User user = new User();
        user.setId(42L);
        user.setEmail("patient@example.com");
        user.setPasswordHash("stored-password-hash");
        user.setEnabled(true);
        user.setRoles(Set.of(Role.PATIENT, Role.DOCTOR));

        when(userRepository.findByEmail("patient@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "stored-password-hash"))
                .thenReturn(true);

        Optional<AuthenticatedActor> result = authenticationService.authenticate(
                "  Patient@Example.COM  ",
                rawPassword);

        assertTrue(result.isPresent());
        assertEquals(42L, result.orElseThrow().userId());
        assertEquals(Set.of(Role.PATIENT, Role.DOCTOR), result.orElseThrow().roles());

        verify(userRepository).findByEmail("patient@example.com");
        verify(passwordEncoder).matches(rawPassword, "stored-password-hash");
    }

    @Test
    void shouldRunValidDummyVerificationWhenEmailDoesNotExist() {
        String rawPassword = "example-credential";

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        Optional<AuthenticatedActor> result = authenticationService.authenticate(
                "  Missing@Example.COM  ",
                rawPassword);

        assertTrue(result.isEmpty());

        ArgumentCaptor<String> encodedPasswordCaptor = ArgumentCaptor.forClass(String.class);

        verify(userRepository).findByEmail("missing@example.com");
        verify(passwordEncoder).matches(
                org.mockito.ArgumentMatchers.eq(rawPassword),
                encodedPasswordCaptor.capture());

        String dummyHash = encodedPasswordCaptor.getValue();

        assertTrue(dummyHash.startsWith("$argon2id$"));

        PasswordEncoder realEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

        boolean matchesDummyHash = assertDoesNotThrow(
                () -> realEncoder.matches(rawPassword, dummyHash));

        assertFalse(matchesDummyHash);
    }

    @Test
    void shouldRejectDisabledUserWithoutVerifyingPassword() {
        User user = new User();
        user.setId(42L);
        user.setEmail("patient@example.com");
        user.setPasswordHash("stored-password-hash");
        user.setEnabled(false);

        when(userRepository.findByEmail("patient@example.com"))
                .thenReturn(Optional.of(user));

        Optional<AuthenticatedActor> result = authenticationService.authenticate(
                "patient@example.com",
                "example-credential");

        assertTrue(result.isEmpty());

        verify(userRepository).findByEmail("patient@example.com");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldRejectWrongPasswordWithoutReadingRoles() {
        String rawPassword = "wrong-credential";

        User user = mock(User.class);

        when(userRepository.findByEmail("patient@example.com"))
                .thenReturn(Optional.of(user));
        when(user.isEnabled()).thenReturn(true);
        when(user.getPasswordHash()).thenReturn("stored-password-hash");
        when(passwordEncoder.matches(rawPassword, "stored-password-hash"))
                .thenReturn(false);

        Optional<AuthenticatedActor> result = authenticationService.authenticate(
                "patient@example.com",
                rawPassword);

        assertTrue(result.isEmpty());

        verify(passwordEncoder).matches(rawPassword, "stored-password-hash");
        verify(user, never()).getRoles();
        verify(user, never()).getId();
    }

    @Test
    void shouldPropagatePasswordEncoderFailure() {
        RuntimeException encoderFailure = new IllegalArgumentException("simulated malformed stored hash");

        User user = new User();
        user.setId(42L);
        user.setEmail("patient@example.com");
        user.setPasswordHash("malformed-password-hash");
        user.setEnabled(true);

        when(userRepository.findByEmail("patient@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "example-credential",
                "malformed-password-hash"))
                .thenThrow(encoderFailure);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> authenticationService.authenticate(
                        "patient@example.com",
                        "example-credential"));

        assertSame(encoderFailure, thrown);
    }

    @Test
    void shouldRejectNullEmailBeforeRepositoryAccess() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> authenticationService.authenticate(
                        null,
                        "example-credential"));

        assertEquals("email must not be null", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void shouldRejectNullPasswordBeforeRepositoryAccess() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> authenticationService.authenticate(
                        "patient@example.com",
                        null));

        assertEquals("password must not be null", exception.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void shouldTreatBlankCredentialsAsGenericAuthenticationFailure() {
        String blankPassword = "   ";

        when(userRepository.findByEmail(""))
                .thenReturn(Optional.empty());

        Optional<AuthenticatedActor> result = authenticationService.authenticate("   ", blankPassword);

        assertTrue(result.isEmpty());

        verify(userRepository).findByEmail("");
        verify(passwordEncoder).matches(
                org.mockito.ArgumentMatchers.eq(blankPassword),
                org.mockito.ArgumentMatchers.startsWith("$argon2id$"));
    }
}