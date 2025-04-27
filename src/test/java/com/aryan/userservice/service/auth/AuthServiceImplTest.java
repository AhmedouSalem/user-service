package com.aryan.userservice.service.auth;

import com.aryan.userservice.dto.OrderRequest;
import com.aryan.userservice.dto.SignupRequest;
import com.aryan.userservice.dto.UserDto;
import com.aryan.userservice.enums.OrderStatus;
import com.aryan.userservice.enums.UserRole;
import com.aryan.userservice.feign.OrderClient;
import com.aryan.userservice.model.User;
import com.aryan.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldCreateUserAndReturnUserDto() {
        // Given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@example.com")
                .name("Test User")
                .password("plaintextPassword")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .password("encodedPassword")
                .role(UserRole.CUSTOMER)
                .build();

        when(passwordEncoder.encode("plaintextPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserDto result = authService.createUser(signupRequest);

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getRole(), result.getUserRole());

        // Verify that the user was saved and order was created
        verify(userRepository, times(1)).save(any(User.class));
        verify(orderClient, times(1)).createOrder(ArgumentMatchers.argThat(order ->
                order.getUserId().equals(savedUser.getId())
                        && order.getAmount().equals(0L)
                        && order.getTotalAmount().equals(0L)
                        && order.getDiscount().equals(0L)
                        && order.getOrderStatus() == OrderStatus.Pending
        ));
    }

    @Test
    void hasUserWithEmail_shouldReturnTrueIfUserExists() {
        // Given
        String email = "exist@example.com";
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(new User()));

        // When
        boolean exists = authService.hasUserWithEmail(email);

        // Then
        assertTrue(exists);
        verify(userRepository, times(1)).findFirstByEmail(email);
    }

    @Test
    void hasUserWithEmail_shouldReturnFalseIfUserDoesNotExist() {
        // Given
        String email = "notfound@example.com";
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean exists = authService.hasUserWithEmail(email);

        // Then
        assertFalse(exists);
        verify(userRepository, times(1)).findFirstByEmail(email);
    }

    @Test
    void createAdminAccount_shouldCreateAdminIfNotExists() {
        // Given
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin")).thenReturn("encodedAdminPassword");

        // When
        authService.createAdminAccount();

        // Then
        verify(userRepository, times(1)).save(argThat(user ->
                user.getEmail().equals("admin@gmail.com") &&
                        user.getName().equals("admin") &&
                        user.getRole() == UserRole.ADMIN &&
                        user.getPassword().equals("encodedAdminPassword")
        ));
    }

    @Test
    void createAdminAccount_shouldNotCreateAdminIfExists() {
        // Given
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(Optional.of(new User()));

        // When
        authService.createAdminAccount();

        // Then
        verify(userRepository, never()).save(any(User.class));
    }


}
