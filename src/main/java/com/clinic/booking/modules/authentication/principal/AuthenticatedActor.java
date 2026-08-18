package com.clinic.booking.modules.authentication.principal;

import java.util.Set;
import java.util.Objects;

import com.clinic.booking.modules.user.entity.Role;

public record AuthenticatedActor(
        Long userId,
        Set<Role> roles) {

    public AuthenticatedActor {
        Objects.requireNonNull(userId, "userId must not be null");

        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be greater than 0");
        }

        Objects.requireNonNull(roles, "roles must not be null");
        roles = Set.copyOf(roles);
    }
}
