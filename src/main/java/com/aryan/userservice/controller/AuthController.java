package com.aryan.userservice.controller;

import com.aryan.userservice.dto.AuthenticationRequest;
import com.aryan.userservice.dto.SignupRequest;
import com.aryan.userservice.dto.UserDto;
import com.aryan.userservice.model.User;
import com.aryan.userservice.repository.UserRepository;
import com.aryan.userservice.service.auth.AuthService;
import com.aryan.userservice.service.jwt.UserDetailsServiceImpl;
import com.aryan.userservice.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsServiceImpl userDetailsService;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final AuthService authService;

	@PostMapping("/authenticate")
	public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
										  HttpServletResponse response) {
		log.info("Received authentication request for user: {}", authenticationRequest.getUsername());

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (Exception e) {
			log.error("Authentication failed for user: {}", authenticationRequest.getUsername(), e);
			throw new BadCredentialsException("incorrect user or pass");
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		Optional<User> user = userRepository.findFirstByEmail(userDetails.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		if (user.isPresent()) {
			try {
				response.getWriter().write(new JSONObject().put("userId", user.get().getId())
						.put("role", user.get().getRole()).toString());
				response.addHeader("Access-Control-Expose-Headers", "Authorization");
				response.addHeader("Access-Control-Allow-Headers", "Authorization, X-PINGOTHER, Origin, "
						+ "X-Requested-With, Content-Type, Accent, X-Custom-header");
				log.info("User ID: {} - Role: {} - Token generated: {}", user.get().getId(), user.get().getRole(), jwt);
			} catch (Exception e) {
				log.error("Error writing response for user: {}", authenticationRequest.getUsername(), e);
			}
			String TOKEN_PREFIX = "Bearer ";
			String HEADER_STRING = "Authorization";
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt);
		} else {
			log.warn("User not found: {}", authenticationRequest.getUsername());
		}
	}

	@GetMapping("/api/users/{userID}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long userID) {
		Optional<User> user = userRepository.findById(userID);
		if (user.isPresent()) {
			UserDto userDto = new UserDto();
			userDto.setId(userID);
			userDto.setId(userID);
			userDto.setEmail(user.get().getEmail());
			userDto.setName(user.get().getName());
			userDto.setUserRole(user.get().getRole());
			return ResponseEntity.ok(userDto);
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUpUser(@RequestBody SignupRequest signupRequest) {
		log.info("Received sign-up request for email: {}", signupRequest.getEmail());

		if (authService.hasUserWithEmail(signupRequest.getEmail())) {
			log.warn("User already exists with email: {}", signupRequest.getEmail());
			return new ResponseEntity<>("user already exists", HttpStatus.NOT_ACCEPTABLE);
		}

		UserDto userDto = authService.createUser(signupRequest);
		log.info("User created with email: {}", signupRequest.getEmail());
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}


}