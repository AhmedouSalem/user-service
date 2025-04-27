package com.aryan.userservice.service.jwt;

import com.aryan.userservice.model.User;
import com.aryan.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsIfUserExists() {
        // Given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .build();

        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findFirstByEmail(email);
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionIfUserDoesNotExist() {
        // Given
        String email = "notfound@example.com";
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        verify(userRepository, times(1)).findFirstByEmail(email);
    }
}
