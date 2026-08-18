package com.clinic.booking.modules.authentication.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.clinic.booking.modules.authentication.dto.request.RegistrationRequest;
import com.clinic.booking.modules.authentication.dto.response.RegistrationResponse;
import com.clinic.booking.modules.authentication.exception.RegistrationEmailAlreadyExistsException;
import com.clinic.booking.modules.authentication.service.RegistrationService;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    void shouldRegisterPatientAccount() throws Exception {
        RegistrationResponse response = new RegistrationResponse(42L, "patient@example.com");

        when(registrationService.register(any(RegistrationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "Patient@Example.COM",
                          "password": "example-credential"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.email").value("patient@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.roles").doesNotExist());

        verify(registrationService)
                .register(any(RegistrationRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationInputIsInvalid() throws Exception {
        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "invalid-email",
                          "password": "short"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email")
                        .value("Email must be valid"))
                .andExpect(jsonPath("$.validationErrors.password")
                        .value("Password must be between 12 and 128 characters long"));

        verifyNoInteractions(registrationService);
    }

    @Test
    void shouldReturnGenericConflictWhenRegistrationEmailAlreadyExists()
            throws Exception {
        when(registrationService.register(any(RegistrationRequest.class)))
                .thenThrow(new RegistrationEmailAlreadyExistsException());

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "patient@example.com",
                          "password": "example-credential"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Unable to create account with the supplied details."))
                .andExpect(jsonPath("$.path").value("/api/registrations"));

        verify(registrationService)
                .register(any(RegistrationRequest.class));
    }
}