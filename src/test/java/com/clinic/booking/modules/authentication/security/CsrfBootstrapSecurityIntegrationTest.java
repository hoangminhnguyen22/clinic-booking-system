package com.clinic.booking.modules.authentication.security;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.clinic.booking.modules.authentication.controller.CsrfController;
import com.clinic.booking.modules.authentication.service.CredentialAuthenticationService;

@WebMvcTest(CsrfController.class)
@Import({
        TestSecurityConfig.class,
        CredentialAuthenticationProvider.class
})
class CsrfBootstrapSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CredentialAuthenticationService authenticationService;

    @Test
    void shouldIssueJavaScriptReadableCsrfCookie() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(cookie().httpOnly("XSRF-TOKEN", false));

        verifyNoInteractions(authenticationService);
    }
}