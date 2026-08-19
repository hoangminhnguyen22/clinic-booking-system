package com.clinic.booking.modules.authentication.security;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.authentication.service.CredentialAuthenticationService;
import com.clinic.booking.modules.user.entity.Role;

@Component
public class CredentialAuthenticationProvider implements AuthenticationProvider {

    private static final String AUTHENTICATION_FAILED = "Authentication failed";

    private final CredentialAuthenticationService authenticationService;

    public CredentialAuthenticationProvider(
            CredentialAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();

        if (!(principal instanceof String email)
                || !(credentials instanceof String password)) {
            throw new BadCredentialsException(AUTHENTICATION_FAILED);
        }

        AuthenticatedActor actor = authenticationService
                .authenticate(email, password)
                .orElseThrow(() -> new BadCredentialsException(AUTHENTICATION_FAILED));

        Collection<GrantedAuthority> authorities = actor.roles()
                .stream()
                .map(this::toAuthority)
                .toList();

        return new AuthenticatedActorToken(actor, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }

    private GrantedAuthority toAuthority(Role role) {
        String authority = switch (role) {
            case PATIENT -> "ROLE_PATIENT";
            case DOCTOR -> "ROLE_DOCTOR";
            case ADMIN -> "ROLE_ADMIN";
        };

        return new SimpleGrantedAuthority(authority);
    }
}