package com.clinic.booking.modules.authentication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.user.entity.Role;

class AuthenticatedActorTokenTest {

    @Test
    void shouldExposeAuthenticatedActorWithoutCredentials() {
        AuthenticatedActor actor = new AuthenticatedActor(
                42L,
                Set.of(Role.PATIENT, Role.DOCTOR));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_PATIENT"),
                new SimpleGrantedAuthority("ROLE_DOCTOR"));

        AuthenticatedActorToken token = new AuthenticatedActorToken(actor, authorities);

        assertSame(actor, token.getPrincipal());
        assertNull(token.getCredentials());
        assertEquals("42", token.getName());
        assertTrue(token.isAuthenticated());
        assertEquals(authorities, List.copyOf(token.getAuthorities()));
    }

    @Test
    void shouldDefensivelyCopyAuthorities() {
        AuthenticatedActor actor = new AuthenticatedActor(42L, Set.of(Role.PATIENT));

        List<GrantedAuthority> mutableAuthorities = new ArrayList<>();
        mutableAuthorities.add(
                new SimpleGrantedAuthority("ROLE_PATIENT"));

        AuthenticatedActorToken token = new AuthenticatedActorToken(actor, mutableAuthorities);

        mutableAuthorities.add(
                new SimpleGrantedAuthority("ROLE_ADMIN"));

        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_PATIENT")),
                List.copyOf(token.getAuthorities()));

        assertThrows(
                UnsupportedOperationException.class,
                () -> token.getAuthorities().clear());
    }

    @Test
    void shouldRejectNullActor() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new AuthenticatedActorToken(
                        null,
                        List.of()));

        assertEquals("actor must not be null", exception.getMessage());
    }
}