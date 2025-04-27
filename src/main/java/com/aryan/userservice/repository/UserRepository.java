package com.aryan.userservice.repository;

import com.aryan.userservice.enums.UserRole;
import com.aryan.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);

    // Only Used to check of ADMIN exists (running for first time)
    Optional<User> findByRole(UserRole userRole);

}
