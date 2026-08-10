package com.clinic.booking.modules.patient.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.clinic.booking.modules.patient.entity.PatientProfile;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PatientProfileRepositoryTest {

    private static final String TEST_CREDENTIAL_PLACEHOLDER = "not-a-real-credential-value";

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindPatientProfileByUserId() {
        User user = saveUser("patient-profile@example.com");

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);

        patientProfileRepository.saveAndFlush(patientProfile);
        Long patientProfileId = patientProfile.getId();

        entityManager.clear();

        Optional<PatientProfile> foundProfile = patientProfileRepository.findByUserId(user.getId());

        assertTrue(foundProfile.isPresent());
        assertNotNull(foundProfile.get().getId());
        assertEquals(patientProfileId, foundProfile.get().getId());
        assertEquals(user.getId(), foundProfile.get().getUser().getId());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoPatientProfile() {
        User user = saveUser("without-profile@example.com");

        Optional<PatientProfile> foundProfile = patientProfileRepository.findByUserId(user.getId());

        assertTrue(foundProfile.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenPatientProfileExistsForUser() {
        User user = saveUser("existing-profile@example.com");

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        patientProfileRepository.saveAndFlush(patientProfile);

        boolean exists = patientProfileRepository.existsByUserId(user.getId());

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenPatientProfileDoesNotExistForUser() {
        User user = saveUser("missing-profile@example.com");

        boolean exists = patientProfileRepository.existsByUserId(user.getId());

        assertFalse(exists);
    }

    @Test
    void shouldRejectSecondPatientProfileForSameUser() {
        User user = saveUser("duplicate-profile@example.com");

        PatientProfile firstProfile = new PatientProfile();
        firstProfile.setUser(user);
        patientProfileRepository.saveAndFlush(firstProfile);

        PatientProfile secondProfile = new PatientProfile();
        secondProfile.setUser(user);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> patientProfileRepository.saveAndFlush(secondProfile));
    }

    @Test
    void shouldRejectPatientProfileWithoutUser() {
        PatientProfile patientProfile = new PatientProfile();

        assertThrows(
                DataIntegrityViolationException.class,
                () -> patientProfileRepository.saveAndFlush(patientProfile));
    }

    @Test
    void shouldDeletePatientProfileWhenUserIsDeleted() {
        User user = saveUser("cascade-delete@example.com");

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(user);
        patientProfileRepository.saveAndFlush(patientProfile);

        Long userId = user.getId();
        Long patientProfileId = patientProfile.getId();

        entityManager.clear();

        userRepository.deleteById(userId);
        userRepository.flush();
        entityManager.clear();

        assertTrue(patientProfileRepository.findById(patientProfileId).isEmpty());
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(TEST_CREDENTIAL_PLACEHOLDER);
        user.getRoles().add(Role.PATIENT);

        return userRepository.saveAndFlush(user);
    }
}