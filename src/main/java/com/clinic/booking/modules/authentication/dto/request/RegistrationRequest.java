package com.clinic.booking.modules.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 254, message = "Email must not exceed 254 characters") String email,

        @NotBlank(message = "Password is required") @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters long") String password) {
}