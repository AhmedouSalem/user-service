package com.aryan.userservice.controller;

import com.aryan.userservice.dto.AuthenticationRequest;
import com.aryan.userservice.dto.SignupRequest;
import com.aryan.userservice.dto.UserDto;
import com.aryan.userservice.enums.UserRole;
import com.aryan.userservice.model.User;
import com.aryan.userservice.service.auth.AuthService;
import com.aryan.userservice.repository.UserRepository;
import com.aryan.userservice.service.jwt.UserDetailsServiceImpl;
import com.aryan.userservice.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 🔧 Dépendances injectées dans AuthController à mocker
    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @Test
    void signUp_shouldReturnUserDtoOnSuccess() throws Exception {
        // Given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("user@example.com")
                .name("User Test")
                .password("password")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("user@example.com")
                .name("User Test")
                .userRole(UserRole.CUSTOMER)
                .build();

        when(authService.hasUserWithEmail("user@example.com")).thenReturn(false);
        when(authService.createUser(any(SignupRequest.class))).thenReturn(userDto);

        // When + Then
        mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.name").value("User Test"))
                .andExpect(jsonPath("$.userRole").value("CUSTOMER"));
    }

    @Test
    void signUp_shouldReturnBadRequestIfEmailExists() throws Exception {
        // Given
        SignupRequest request = SignupRequest.builder()
                .email("existing@example.com")
                .password("pass123")
                .name("John Doe")
                .build();

        // Simuler comportement du service
        when(authService.hasUserWithEmail("existing@example.com")).thenReturn(true);
        when(authService.createUser(any())).thenThrow(new IllegalStateException("Should not be called"));

        // When
        MvcResult result = mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        int status = result.getResponse().getStatus();

        // Then
        assertThat(status).isIn(400, 406); // On accepte les deux
    }

    @Test
    void authenticate_shouldReturnJwtAndUserInfoOnSuccess() throws Exception {
        // Given
        String email = "user@example.com";
        String password = "password";

        AuthenticationRequest request = AuthenticationRequest.builder()
                .username(email)
                .password(password)
                .build();

        User user = User.builder()
                .id(42L)
                .email(email)
                .password("encodedPassword")
                .role(UserRole.CUSTOMER)
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(email)).thenReturn("mocked-jwt-token");

        // La partie `authenticationManager.authenticate(...)` est void → on ne mocke pas de retour, mais on doit éviter qu’elle jette une exception.
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(org.springframework.security.core.Authentication.class));

        // When + Then
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer mocked-jwt-token"))
                .andExpect(jsonPath("$.userId").value(42L))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void getUserById_shouldReturnUserDtoIfExists() throws Exception {
        // Given
        long userId = 42L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole(UserRole.CUSTOMER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When + Then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.userRole").value("CUSTOMER"));
    }

    @Test
    void getUserById_shouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        // Given
        long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When + Then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }


}
