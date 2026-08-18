package com.clinic.booking.modules.authentication.service.impl;

import java.util.Optional;
import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.booking.modules.authentication.service.CredentialAuthenticationService;
import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.user.repository.UserRepository;
import com.clinic.booking.modules.user.entity.User;

@Service
public class CredentialAuthenticationServiceImpl implements CredentialAuthenticationService {
    // Valid Argon2id encoding used only to reduce unknown-email timing differences.
    private static final String DUMMY_PASSWORD_HASH = "$argon2id$v=19$m=16384,t=2,p=1$"
            + "c29tZXNhbHQxMjM0NTY3OA$"
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CredentialAuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthenticatedActor> authenticate(String email, String password) {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(password, "password must not be null");

        String canonicalEmail = email.trim().toLowerCase(Locale.ROOT);
        Optional<User> optionalUser = userRepository.findByEmail(canonicalEmail);

        if (optionalUser.isEmpty()) {
            passwordEncoder.matches(password, DUMMY_PASSWORD_HASH);
            return Optional.empty();
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return Optional.empty();
        }

        AuthenticatedActor actor = new AuthenticatedActor(user.getId(), user.getRoles());

        return Optional.of(actor);
    }
}
