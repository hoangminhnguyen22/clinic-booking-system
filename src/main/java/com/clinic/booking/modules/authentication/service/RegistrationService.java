package com.clinic.booking.modules.authentication.service;

import com.clinic.booking.modules.authentication.dto.request.RegistrationRequest;
import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;

public interface RegistrationService {
    RegistrationResponse register(RegistrationRequest request);
}
