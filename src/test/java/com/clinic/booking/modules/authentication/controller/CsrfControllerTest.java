package com.clinic.booking.modules.authentication.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;

class CsrfControllerTest {

    @Test
    void shouldMaterializeCsrfToken() {
        CsrfToken csrfToken = mock(CsrfToken.class);
        CsrfController controller = new CsrfController();

        controller.csrf(csrfToken);

        verify(csrfToken).getToken();
    }
}