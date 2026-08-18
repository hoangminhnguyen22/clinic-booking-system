package com.clinic.booking.modules.authentication.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;
import com.clinic.booking.modules.user.entity.User;

class RegistrationMapperTest {

    private final RegistrationMapper mapper = new RegistrationMapper();

    @Test
    void shouldMapUserToRegistrationResponse() {
        User user = new User();
        user.setId(42L);
        user.setEmail("patient@example.com");

        RegistrationResponse response = mapper.toResponse(user);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.email()).isEqualTo("patient@example.com");
    }
}