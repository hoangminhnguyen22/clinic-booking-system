package com.clinic.booking.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

    private final PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder();

    @Test
    void shouldEncodeAndMatchPasswordWithArgon2() {
        String rawPassword = "example-credential";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encodedPassword).startsWith("$argon2");
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("different-credential", encodedPassword)).isFalse();
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "example-credential";

        String firstHash = passwordEncoder.encode(rawPassword);
        String secondHash = passwordEncoder.encode(rawPassword);

        assertThat(firstHash).isNotEqualTo(secondHash);
        assertThat(passwordEncoder.matches(rawPassword, firstHash)).isTrue();
        assertThat(passwordEncoder.matches(rawPassword, secondHash)).isTrue();
    }
}
