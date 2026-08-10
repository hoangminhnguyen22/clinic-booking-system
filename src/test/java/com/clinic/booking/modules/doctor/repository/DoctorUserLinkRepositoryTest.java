package com.clinic.booking.modules.doctor.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.clinic.booking.modules.doctor.entity.Doctor;
import com.clinic.booking.modules.specialty.entity.Specialty;
import com.clinic.booking.modules.user.entity.Role;
import com.clinic.booking.modules.user.entity.User;
import com.clinic.booking.modules.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DoctorUserLinkRepositoryTest {

    private static final String TEST_CREDENTIAL_PLACEHOLDER = "not-a-real-credential-value";

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveDoctorWithoutLinkedUser() {
        Specialty specialty = saveSpecialty("Unlinked Specialty");
        Doctor doctor = createDoctor(
                "Dr. Unlinked",
                "unlinked.doctor@example.com",
                specialty);

        Long doctorId = doctorRepository.saveAndFlush(doctor).getId();

        entityManager.clear();

        Doctor savedDoctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        assertNull(savedDoctor.getUser());
    }

    @Test
    void shouldSaveDoctorLinkedToUser() {
        Specialty specialty = saveSpecialty("Linked Specialty");
        User user = saveDoctorUser("linked.user@example.com");

        Doctor doctor = createDoctor(
                "Dr. Linked",
                "linked.doctor@example.com",
                specialty);
        doctor.setUser(user);

        Long doctorId = doctorRepository.saveAndFlush(doctor).getId();

        entityManager.clear();

        Doctor savedDoctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        assertEquals(user.getId(), savedDoctor.getUser().getId());
    }

    @Test
    void shouldRejectLinkingSameUserToTwoDoctors() {
        Specialty specialty = saveSpecialty("Unique Link Specialty");
        User user = saveDoctorUser("unique-link.user@example.com");

        Doctor firstDoctor = createDoctor(
                "Dr. First",
                "first.unique-link@example.com",
                specialty);
        firstDoctor.setUser(user);
        doctorRepository.saveAndFlush(firstDoctor);

        Doctor secondDoctor = createDoctor(
                "Dr. Second",
                "second.unique-link@example.com",
                specialty);
        secondDoctor.setUser(user);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> doctorRepository.saveAndFlush(secondDoctor));
    }

    @Test
    void shouldUnlinkDoctorWithoutDeletingDoctorWhenUserIsDeleted() {
        Specialty specialty = saveSpecialty("Delete Link Specialty");
        User user = saveDoctorUser("delete-link.user@example.com");

        Doctor doctor = createDoctor(
                "Dr. Preserved",
                "preserved.doctor@example.com",
                specialty);
        doctor.setUser(user);

        Long doctorId = doctorRepository.saveAndFlush(doctor).getId();
        Long userId = user.getId();

        entityManager.clear();

        userRepository.deleteById(userId);
        userRepository.flush();
        entityManager.clear();

        assertTrue(doctorRepository.findById(doctorId).isPresent());

        Doctor preservedDoctor = doctorRepository.findById(doctorId)
                .orElseThrow();

        assertNull(preservedDoctor.getUser());
    }

    @Test
    void shouldFindDoctorByLinkedUserId() {
        Specialty specialty = saveSpecialty("Find Doctor Specialty");
        User user = saveDoctorUser("find.doctor.user@example.com");

        Doctor doctor = createDoctor(
                "Dr. Found",
                "found.doctor@example.com",
                specialty);
        doctor.setUser(user);

        Long doctorId = doctorRepository.saveAndFlush(doctor).getId();

        entityManager.clear();

        Doctor foundDoctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow();

        assertEquals(doctorId, foundDoctor.getId());
        assertEquals(user.getId(), foundDoctor.getUser().getId());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoDoctorProfile() {
        User user = saveDoctorUser("without.doctor.profile@example.com");

        entityManager.clear();

        Optional<Doctor> foundDoctor = doctorRepository.findByUserId(user.getId());

        assertTrue(foundDoctor.isEmpty());
    }

    private Specialty saveSpecialty(String name) {
        Specialty specialty = new Specialty(name);
        entityManager.persist(specialty);
        entityManager.flush();
        return specialty;
    }

    private User saveDoctorUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(TEST_CREDENTIAL_PLACEHOLDER);
        user.getRoles().add(Role.DOCTOR);
        user.getRoles().add(Role.PATIENT);
        return userRepository.saveAndFlush(user);
    }

    private Doctor createDoctor(
            String fullName,
            String email,
            Specialty specialty) {

        Doctor doctor = new Doctor();
        doctor.setFullName(fullName);
        doctor.setEmail(email);
        doctor.setSpecialty(specialty);
        return doctor;
    }
}