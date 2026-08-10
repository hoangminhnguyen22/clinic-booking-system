package com.clinic.booking.modules.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

    private static final String TEST_CREDENTIAL_PLACEHOLDER = "not-a-real-credential-value";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = createUser("patient@example.com");
        user.getRoles().add(Role.PATIENT);

        userRepository.saveAndFlush(user);
        entityManager.clear();

        Optional<User> foundUser = userRepository.findByEmail(
                "patient@example.com");

        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getId());
        assertEquals("patient@example.com", foundUser.get().getEmail());
        assertEquals(
                TEST_CREDENTIAL_PLACEHOLDER,
                foundUser.get().getPasswordHash());
        assertTrue(foundUser.get().isEnabled());
        assertNotNull(foundUser.get().getCreatedAt());
        assertNotNull(foundUser.get().getUpdatedAt());
        assertEquals(
                foundUser.get().getCreatedAt(),
                foundUser.get().getUpdatedAt());
        assertEquals(1, foundUser.get().getRoles().size());
        assertTrue(foundUser.get().getRoles().contains(Role.PATIENT));
    }

    @Test
    void shouldPersistMultipleRolesForUser() {
        User user = createUser("doctor@example.com");
        user.getRoles().add(Role.DOCTOR);
        user.getRoles().add(Role.PATIENT);

        userRepository.saveAndFlush(user);
        entityManager.clear();

        User foundUser = userRepository.findByEmail("doctor@example.com")
                .orElseThrow();

        assertEquals(2, foundUser.getRoles().size());
        assertTrue(foundUser.getRoles().contains(Role.DOCTOR));
        assertTrue(foundUser.getRoles().contains(Role.PATIENT));
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> foundUser = userRepository.findByEmail(
                "missing@example.com");

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = createUser("existing@example.com");
        user.getRoles().add(Role.PATIENT);
        userRepository.saveAndFlush(user);

        boolean exists = userRepository.existsByEmail(
                "existing@example.com");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail(
                "missing@example.com");

        assertFalse(exists);
    }

    @Test
    void shouldRejectDuplicateEmail() {
        User firstUser = createUser("duplicate@example.com");
        firstUser.getRoles().add(Role.PATIENT);
        userRepository.saveAndFlush(firstUser);

        User duplicateUser = createUser("duplicate@example.com");
        duplicateUser.getRoles().add(Role.PATIENT);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(duplicateUser));
    }

    @Test
    void shouldRejectNonCanonicalEmail() {
        User user = createUser(" Patient@Example.COM ");
        user.getRoles().add(Role.PATIENT);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(user));
    }

    @Test
    void shouldUpdateUpdatedAtWithoutChangingCreatedAt() {
        User user = createUser("updated@example.com");
        user.getRoles().add(Role.PATIENT);
        Long userId = userRepository.saveAndFlush(user).getId();

        entityManager.clear();

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow();
        Instant originalCreatedAt = userToUpdate.getCreatedAt();

        userToUpdate.setEnabled(false);
        userToUpdate.setUpdatedAt(Instant.EPOCH);
        userRepository.saveAndFlush(userToUpdate);

        entityManager.clear();

        User updatedUser = userRepository.findById(userId)
                .orElseThrow();

        assertFalse(updatedUser.isEnabled());
        assertEquals(originalCreatedAt, updatedUser.getCreatedAt());
        assertTrue(updatedUser.getUpdatedAt().isAfter(Instant.EPOCH));
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(TEST_CREDENTIAL_PLACEHOLDER);
        return user;
    }
}