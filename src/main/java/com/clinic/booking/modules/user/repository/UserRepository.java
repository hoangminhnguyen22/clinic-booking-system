package com.clinic.booking.modules.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinic.booking.modules.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
