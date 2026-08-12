package com.clinic.booking.modules.authentication.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RegistrationRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidRequest() {
        RegistrationRequest request = new RegistrationRequest("patient@example.com", "example-credential");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectBlankEmail() {
        RegistrationRequest request = new RegistrationRequest("   ", "example-credential");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage()).isEqualTo("Email is required");
                });
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        RegistrationRequest request = new RegistrationRequest("invalid-email", "example-credential");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage()).isEqualTo("Email must be valid");
                });
    }

    @Test
    void shouldRejectEmailLongerThan254Characters() {
        String email = "a".repeat(243) + "@example.com";
        RegistrationRequest request = new RegistrationRequest(email, "example-credential");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(email).hasSize(255);
        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
                    assertThat(violation.getMessage())
                            .isEqualTo("Email must not exceed 254 characters");
                });
    }

    @Test
    void shouldRejectBlankPassword() {
        RegistrationRequest request = new RegistrationRequest("patient@example.com", "   ");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
                    assertThat(violation.getMessage()).isEqualTo("Password is required");
                });
    }

    @Test
    void shouldRejectPasswordShorterThan12Characters() {
        String password = "a".repeat(11);
        RegistrationRequest request = new RegistrationRequest("patient@example.com", password);

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(password).hasSize(11);
        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
                    assertThat(violation.getMessage())
                            .isEqualTo("Password must be between 12 and 128 characters long");
                });
    }

    @Test
    void shouldAcceptPasswordWith12Characters() {
        String password = "a".repeat(12);
        RegistrationRequest request = new RegistrationRequest("patient@example.com", password);

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(password).hasSize(12);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptPasswordWith128Characters() {
        String password = "a".repeat(128);
        RegistrationRequest request = new RegistrationRequest("patient@example.com", password);

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(password).hasSize(128);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectPasswordLongerThan128Characters() {
        String password = "a".repeat(129);
        RegistrationRequest request = new RegistrationRequest("patient@example.com", password);

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertThat(password).hasSize(129);
        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
                    assertThat(violation.getMessage())
                            .isEqualTo("Password must be between 12 and 128 characters long");
                });
    }
}