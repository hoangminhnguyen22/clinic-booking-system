package com.clinic.booking.modules.authentication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.clinic.booking.modules.authentication.controller.CsrfController;
import com.clinic.booking.modules.authentication.principal.AuthenticatedActor;
import com.clinic.booking.modules.authentication.service.CredentialAuthenticationService;
import com.clinic.booking.modules.user.entity.Role;

@WebMvcTest(CsrfController.class)
@Import({
        TestSecurityConfig.class,
        CredentialAuthenticationProvider.class
})
class JsonLoginSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CredentialAuthenticationService authenticationService;

    @Test
    void shouldStoreAuthenticatedActorInHttpSessionAfterSuccessfulLogin()
            throws Exception {
        String email = "  Patient@Example.COM  ";
        String password = " example-credential ";

        AuthenticatedActor actor = new AuthenticatedActor(
                42L,
                Set.of(Role.PATIENT, Role.DOCTOR));

        when(authenticationService.authenticate(email, password))
                .thenReturn(Optional.of(actor));

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "  Patient@Example.COM  ",
                          "password": " example-credential "
                        }
                        """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        assertTrue(session != null);

        Object storedContext = session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        SecurityContext securityContext = assertInstanceOf(SecurityContext.class, storedContext);

        AuthenticatedActorToken token = assertInstanceOf(
                AuthenticatedActorToken.class,
                securityContext.getAuthentication());

        assertSame(actor, token.getPrincipal());
        assertNull(token.getCredentials());
        assertEquals("42", token.getName());
        assertTrue(token.isAuthenticated());

        Set<String> authorities = token.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        assertEquals(
                Set.of("ROLE_PATIENT", "ROLE_DOCTOR"),
                authorities);

        verify(authenticationService).authenticate(email, password);
    }

    @Test
    void shouldReturnGenericUnauthorizedWhenCredentialsAreRejected()
            throws Exception {
        String email = "patient@example.com";
        String password = "wrong-credential";

        when(authenticationService.authenticate(email, password))
                .thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "patient@example.com",
                          "password": "wrong-credential"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        if (session != null) {
            assertNull(session.getAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
        }

        verify(authenticationService).authenticate(email, password);
    }

    @Test
    void shouldRejectLoginWithoutCsrfBeforeCredentialAuthentication()
            throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "patient@example.com",
                          "password": "example-credential"
                        }
                        """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(authenticationService);
    }

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