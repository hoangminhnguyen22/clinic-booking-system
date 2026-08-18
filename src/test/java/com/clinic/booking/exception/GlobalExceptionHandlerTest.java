package com.clinic.booking.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import com.clinic.booking.modules.authentication.exception.RegistrationEmailAlreadyExistsException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapRegistrationEmailConflictToGeneric409Response() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/registrations");

        RegistrationEmailAlreadyExistsException exception = new RegistrationEmailAlreadyExistsException();

        ResponseEntity<ApiErrorResponse> response = handler.handleRegistrationEmailAlreadyExists(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
        assertThat(response.getBody().message())
                .isEqualTo("Unable to create account with the supplied details.");
        assertThat(response.getBody().message()).doesNotContain("@");
        assertThat(response.getBody().path()).isEqualTo("/api/registrations");
    }
}