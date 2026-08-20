package com.clinic.booking.modules.authentication.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.http.MediaType;

import com.clinic.booking.exception.ApiErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    AuthenticationManager authenticationManager(
            CredentialAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    JsonLoginAuthenticationFilter jsonLoginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            ObjectMapper objectMapper,
            SecurityContextRepository securityContextRepository) {
        JsonLoginAuthenticationFilter filter = new JsonLoginAuthenticationFilter(
                authenticationManager,
                objectMapper);

        filter.setSecurityContextRepository(securityContextRepository);

        filter.setAuthenticationSuccessHandler(
                (request, response, authentication) -> response
                        .setStatus(HttpStatus.NO_CONTENT.value()));

        filter.setAuthenticationFailureHandler(
                (request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()));

        return filter;
    }

    @Bean
    SecurityFilterChain testSecurityFilterChain(
            HttpSecurity http,
            JsonLoginAuthenticationFilter jsonLoginAuthenticationFilter,
            SecurityContextRepository securityContextRepository,
            ObjectMapper objectMapper)
            throws Exception {
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        http
                .securityContext(context -> context
                        .securityContextRepository(
                                securityContextRepository))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/csrf",
                                "/api/auth/logout")
                        .permitAll()
                        .requestMatchers("/test/admin")
                        .hasRole("ADMIN")
                        .requestMatchers("/test/**")
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authenticationException) -> writeErrorResponse(
                                        request,
                                        response,
                                        objectMapper,
                                        HttpStatus.UNAUTHORIZED,
                                        "Authentication is required."))
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> writeErrorResponse(
                                        request,
                                        response,
                                        objectMapper,
                                        HttpStatus.FORBIDDEN,
                                        "Access is denied.")))
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(
                                (request, response, authentication) -> response.setStatus(
                                        HttpStatus.NO_CONTENT.value()))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterAt(
                        jsonLoginAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeErrorResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            ObjectMapper objectMapper,
            HttpStatus status,
            String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}