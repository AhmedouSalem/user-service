package com.aryan.userservice.service.auth;

import com.aryan.userservice.dto.SignupRequest;
import com.aryan.userservice.dto.UserDto;

public interface AuthService {
	UserDto createUser(SignupRequest signupRequest);

	Boolean hasUserWithEmail(String email);

	void createAdminAccount();
}
