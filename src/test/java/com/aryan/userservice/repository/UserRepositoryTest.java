package com.aryan.userservice.repository;

import com.aryan.userservice.enums.UserRole;
import com.aryan.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findFirstByEmail_shouldReturnUserIfExists() {
        // Given
        User user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .password("pwd")
                .role(UserRole.CUSTOMER)
                .build();

        userRepository.save(user);

        // When
        Optional<User> result = userRepository.findFirstByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findFirstByEmail_shouldReturnEmptyIfNotExists() {
        // When
        Optional<User> result = userRepository.findFirstByEmail("notfound@example.com");

        // Then
        assertThat(result).isNotPresent();
    }

    @Test
    void findByRole_shouldReturnUserIfRoleExists() {
        // Given
        User user = User.builder()
                .email("admin@example.com")
                .name("Admin User")
                .password("admin")
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(user);

        // When
        Optional<User> result = userRepository.findByRole(UserRole.ADMIN);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findByRole_shouldReturnEmptyIfRoleNotExists() {
        // When
        Optional<User> result = userRepository.findByRole(UserRole.ADMIN);

        // Then
        assertThat(result).isNotPresent();
    }
}
