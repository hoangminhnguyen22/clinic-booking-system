package com.clinic.booking.modules.authentication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

class JsonLoginAuthenticationFilterTest {

    private AuthenticationManager authenticationManager;
    private JsonLoginAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        filter = new JsonLoginAuthenticationFilter(
                authenticationManager,
                new ObjectMapper());
    }

    @Test
    void shouldCreateUnauthenticatedTokenFromJsonCredentials() throws Exception {
        String email = "  Patient@Example.COM  ";
        String password = " example-credential ";

        MockHttpServletRequest request = jsonRequest("""
                {
                  "email": "  Patient@Example.COM  ",
                  "password": " example-credential "
                }
                """);

        MockHttpServletResponse response = new MockHttpServletResponse();

        Authentication expectedResult = mock(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(expectedResult);

        Authentication result = filter.attemptAuthentication(request, response);

        assertSame(expectedResult, result);

        var authenticationCaptor = org.mockito.ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager)
                .authenticate(authenticationCaptor.capture());

        Authentication capturedAuthentication = authenticationCaptor.getValue();

        UsernamePasswordAuthenticationToken inputToken = assertInstanceOf(
                UsernamePasswordAuthenticationToken.class,
                capturedAuthentication);

        assertEquals(email, inputToken.getPrincipal());
        assertEquals(password, inputToken.getCredentials());
        assertFalse(inputToken.isAuthenticated());
    }

    @Test
    void shouldRejectNonJsonContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");

        request.setContentType(MediaType.TEXT_PLAIN_VALUE);
        request.setContent("""
                {
                  "email": "patient@example.com",
                  "password": "example-credential"
                }
                """.getBytes(StandardCharsets.UTF_8));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> filter.attemptAuthentication(
                        request,
                        new MockHttpServletResponse()));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldRejectMalformedJson() {
        MockHttpServletRequest request = jsonRequest("{not-valid-json");

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> filter.attemptAuthentication(
                        request,
                        new MockHttpServletResponse()));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldRejectJsonRootThatIsNotAnObject() {
        MockHttpServletRequest request = jsonRequest("""
                [
                  "patient@example.com",
                  "example-credential"
                ]
                """);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> filter.attemptAuthentication(
                        request,
                        new MockHttpServletResponse()));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldRejectMissingCredentialField() {
        MockHttpServletRequest request = jsonRequest("""
                {
                  "email": "patient@example.com"
                }
                """);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> filter.attemptAuthentication(
                        request,
                        new MockHttpServletResponse()));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void shouldRejectCredentialFieldThatIsNotText() {
        MockHttpServletRequest request = jsonRequest("""
                {
                  "email": "patient@example.com",
                  "password": 123
                }
                """);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> filter.attemptAuthentication(
                        request,
                        new MockHttpServletResponse()));

        assertEquals("Authentication failed", exception.getMessage());
        verifyNoInteractions(authenticationManager);
    }

    private MockHttpServletRequest jsonRequest(String body) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");

        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setContent(body.getBytes(StandardCharsets.UTF_8));

        return request;
    }
}