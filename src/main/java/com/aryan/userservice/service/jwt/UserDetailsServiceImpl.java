package com.aryan.userservice.service.jwt;

import com.aryan.userservice.model.User;
import com.aryan.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Attempting to load user by username '{}'", username);
		Optional<User> optionalUser = userRepository.findFirstByEmail(username);
		if (optionalUser.isEmpty()) {
			log.error("User with username '{}' not found", username);
			throw new UsernameNotFoundException("username not found", null);
		}
		log.info("User with username '{}' found", username);
		return new org.springframework.security.core.userdetails.User(optionalUser.get().getEmail(),
				optionalUser.get().getPassword(), new ArrayList<>());
	}

}

