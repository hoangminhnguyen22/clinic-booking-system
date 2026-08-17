package com.clinic.booking.modules.authentication.principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.clinic.booking.modules.user.entity.Role;

class AuthenticatedActorTest {

    @Test
    void shouldExposeUserIdAndRoles() {
        AuthenticatedActor actor = new AuthenticatedActor(
                1L,
                Set.of(Role.PATIENT, Role.DOCTOR));

        assertEquals(1L, actor.userId());
        assertEquals(Set.of(Role.PATIENT, Role.DOCTOR), actor.roles());
    }

    @Test
    void shouldDefensivelyCopyRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.PATIENT);

        AuthenticatedActor actor = new AuthenticatedActor(1L, roles);
        roles.add(Role.ADMIN);

        assertEquals(Set.of(Role.PATIENT), actor.roles());
    }

    @Test
    void shouldExposeUnmodifiableRoles() {
        AuthenticatedActor actor = new AuthenticatedActor(1L, Set.of(Role.PATIENT));

        assertThrows(
                UnsupportedOperationException.class,
                () -> actor.roles().add(Role.ADMIN));
    }

    @Test
    void shouldRejectNullUserId() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new AuthenticatedActor(null, Set.of(Role.PATIENT)));

        assertEquals("userId must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullRoles() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new AuthenticatedActor(1L, null));

        assertEquals("roles must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullRoleElement() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.PATIENT);
        roles.add(null);

        assertThrows(
                NullPointerException.class,
                () -> new AuthenticatedActor(1L, roles));
    }

    @Test
    void shouldRejectZeroUserId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AuthenticatedActor(0L, Set.of(Role.PATIENT)));

        assertEquals("userId must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeUserId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AuthenticatedActor(-1L, Set.of(Role.PATIENT)));

        assertEquals("userId must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldAllowEmptyRoles() {
        AuthenticatedActor actor = new AuthenticatedActor(1L, Set.of());

        assertEquals(Set.of(), actor.roles());
    }
}