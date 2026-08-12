package com.clinic.booking.modules.authentication.mapper;

import org.springframework.stereotype.Component;

import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;
import com.clinic.booking.modules.user.entity.User;

@Component
public class RegistrationMapper {
    public RegistrationResponse toResponse(User user) {
        return new RegistrationResponse(
                user.getId(),
                user.getEmail());
    }
}
