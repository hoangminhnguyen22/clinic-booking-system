package com.clinic.booking.modules.authentication.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class JsonLoginAuthenticationFilter
        extends AbstractAuthenticationProcessingFilter {

    private static final String AUTHENTICATION_FAILED = "Authentication failed";

    private final ObjectMapper objectMapper;

    public JsonLoginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            ObjectMapper objectMapper) {
        super(PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, "/api/auth/login"));
        setAuthenticationManager(Objects.requireNonNull(
                authenticationManager,
                "authenticationManager must not be null"));
        this.objectMapper = Objects.requireNonNull(
                objectMapper,
                "objectMapper must not be null");
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!isJsonRequest(request)) {
            throw authenticationFailed();
        }

        JsonNode body;

        try {
            body = objectMapper.readTree(request.getInputStream());
        } catch (JsonProcessingException exception) {
            throw authenticationFailed();
        }

        if (body == null || !body.isObject()) {
            throw authenticationFailed();
        }

        JsonNode emailNode = body.get("email");
        JsonNode passwordNode = body.get("password");

        if (emailNode == null
                || !emailNode.isTextual()
                || passwordNode == null
                || !passwordNode.isTextual()) {
            throw authenticationFailed();
        }

        String email = emailNode.textValue();
        String password = passwordNode.textValue();

        UsernamePasswordAuthenticationToken authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                email,
                password);

        authenticationRequest.setDetails(
                authenticationDetailsSource.buildDetails(request));

        return getAuthenticationManager().authenticate(authenticationRequest);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();

        if (contentType == null) {
            return false;
        }

        try {
            return MediaType.APPLICATION_JSON.isCompatibleWith(
                    MediaType.parseMediaType(contentType));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private BadCredentialsException authenticationFailed() {
        return new BadCredentialsException(AUTHENTICATION_FAILED);
    }
}