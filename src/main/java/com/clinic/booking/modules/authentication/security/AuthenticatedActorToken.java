package com.clinic.booking.modules.authentication.security;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;

public final class AuthenticatedActorToken extends AbstractAuthenticationToken {

    private final AuthenticatedActor actor;

    public AuthenticatedActorToken(
            AuthenticatedActor actor,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.actor = Objects.requireNonNull(actor, "actor must not be null");
        setAuthenticated(true);
    }

    @Override
    public AuthenticatedActor getPrincipal() {
        return actor;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getName() {
        return actor.userId().toString();
    }
}