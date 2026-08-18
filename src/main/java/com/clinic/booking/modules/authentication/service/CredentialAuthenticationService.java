package com.clinic.booking.modules.authentication.service;

import java.util.Optional;

import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;

public interface CredentialAuthenticationService {
    Optional<AuthenticatedActor> authenticate(String email, String password);
}
