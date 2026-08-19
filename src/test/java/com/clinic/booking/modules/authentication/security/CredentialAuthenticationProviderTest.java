package com.clinic.booking.modules.authentication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.authentication.service.CredentialAuthenticationService;
import com.clinic.booking.modules.user.entity.Role;

@ExtendWith(MockitoExtension.class)
class CredentialAuthenticationProviderTest {

    @Mock
    private CredentialAuthenticationService authenticationService;

    private CredentialAuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        authenticationProvider = new CredentialAuthenticationProvider(authenticationService);
    }

    @Test
    void shouldAuthenticateCredentialsAndMapCurrentRolesToAuthorities() {
        String email = "Patient@Example.COM";
        String password = " example-credential ";

        AuthenticatedActor actor = new AuthenticatedActor(
                42L,
                Set.of(Role.PATIENT, Role.DOCTOR, Role.ADMIN));

        when(authenticationService.authenticate(email, password))
                .thenReturn(Optional.of(actor));

        Authentication input = UsernamePasswordAuthenticationToken.unauthenticated(
                email,
                password);

        Authentication result = authenticationProvider.authenticate(input);

        AuthenticatedActorToken token = assertInstanceOf(AuthenticatedActorToken.class, result);

        assertSame(actor, token.getPrincipal());
        assertNull(token.getCredentials());
        assertEquals("42", token.getName());
        assertTrue(token.isAuthenticated());

        Set<String> authorities = token.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        assertEquals(
                Set.of(
                        "ROLE_PATIENT",
                        "ROLE_DOCTOR",
                        "ROLE_ADMIN"),
                authorities);

        verify(authenticationService).authenticate(email, password);
    }

    @Test
    void shouldRejectGenericCredentialFailure() {
        String email = "patient@example.com";
        String password = "example-credential";

        when(authenticationService.authenticate(email, password))
                .thenReturn(Optional.empty());

        Authentication input = UsernamePasswordAuthenticationToken.unauthenticated(
                email,
                password);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationProvider.authenticate(input));

        assertEquals("Authentication failed", exception.getMessage());
        verify(authenticationService).authenticate(email, password);
    }

    @Test
    void shouldRejectNonStringPrincipalBeforeCallingService() {
        Authentication input = UsernamePasswordAuthenticationToken.unauthenticated(
                42L,
                "example-credential");

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationProvider.authenticate(input));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationService);
    }

    @Test
    void shouldRejectNonStringCredentialsBeforeCallingService() {
        Authentication input = UsernamePasswordAuthenticationToken.unauthenticated(
                "patient@example.com",
                new char[] { 'n', 'o', 't', '-', 'a', '-', 's', 't', 'r', 'i', 'n', 'g' });

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationProvider.authenticate(input));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationService);
    }

    @Test
    void shouldPropagateInternalAuthenticationServiceFailure() {
        RuntimeException internalFailure = new IllegalStateException("simulated internal failure");

        String email = "patient@example.com";
        String password = "example-credential";

        when(authenticationService.authenticate(email, password))
                .thenThrow(internalFailure);

        Authentication input = UsernamePasswordAuthenticationToken.unauthenticated(
                email,
                password);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> authenticationProvider.authenticate(input));

        assertSame(internalFailure, thrown);
    }

    @Test
    void shouldSupportUsernamePasswordAuthenticationTokensOnly() {
        assertTrue(authenticationProvider.supports(
                UsernamePasswordAuthenticationToken.class));

        assertFalse(authenticationProvider.supports(
                TestingAuthenticationToken.class));
    }
}