package com.aryan.userservice.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String testSecret = "VEhJU0lTQVZFRVJZTE9OR0FORFNFQ1VSRVNFQ1JFVEtFWUZPUkpXVEdFTkVSQVRJT05CWUFSWUFOUEFUSUwxVEhJU0lTQVZFRVJZTE9OR0FORFNFQ1VSRVNFQ1JFVEtFWUZPUkpXVEdFTkVSQVRJT05CWUFSWUFOUEFUSUwy";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();
        jwtUtil.setSecret(testSecret);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        // Given
        String username = "test@example.com";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        // Given
        String username = "test@example.com";
        String token = jwtUtil.generateToken(username);

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        // Given
        String username = "test@example.com";
        UserDetails userDetails = User.withUsername(username).password("pwd").authorities("ROLE_USER").build();
        String token = jwtUtil.generateToken(username);

        // When
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForWrongUser() {
        // Given
        String token = jwtUtil.generateToken("user1@example.com");
        UserDetails userDetails = User.withUsername("user2@example.com").password("pwd").authorities("ROLE_USER").build();

        // When
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertFalse(isValid);
    }
}
