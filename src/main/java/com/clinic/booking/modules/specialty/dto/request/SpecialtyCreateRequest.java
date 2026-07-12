package com.clinic.booking.modules.specialty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialtyCreateRequest(
                @NotBlank(message = "Specialty name must not be blank") @Size(max = 100, message = "Specialty name must not exceed 100 characters") String name) {
        public SpecialtyCreateRequest {
                if (name != null) {
                        name = name.trim();
                }
        }
}
