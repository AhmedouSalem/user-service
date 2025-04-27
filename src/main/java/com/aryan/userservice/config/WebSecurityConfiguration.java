package com.aryan.userservice.config;

import com.aryan.userservice.filters.JwtRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfiguration {

	@Value("${ecom.token}")
	private String ecomToken;

	private final JwtRequestFilter jwtRequestFilter;

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
		log.info("Configuring security filter chain");

		http.csrf(csrf -> {
			log.info("Disabling CSRF protection");
			csrf.disable();
		});

		http.authorizeHttpRequests(auth -> {
			auth.requestMatchers(
							mvc.pattern("/authenticate"),
							mvc.pattern("/sign-up"),
							mvc.pattern("/order/**"),
							mvc.pattern("/v3/api-docs"),
							mvc.pattern("/swagger-resources/**"),
							mvc.pattern("/swagger-ui/**"),
							mvc.pattern("/webjars/**")
					).permitAll()
					.requestMatchers(mvc.pattern("/api/**")).permitAll().anyRequest().authenticated();
		});


		http.sessionManagement(sessionManagement -> {
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});

		// Ajout d’un filtre custom
		http.addFilterBefore((servletRequest, servletResponse, filterChain) -> {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			String authHeader = request.getHeader("Authorization");
			String userHeader = request.getHeader("X-User");

			System.out.println("==> Incoming request to UserService");
			System.out.println("Authorization: " + authHeader);
			System.out.println("X-User: " + userHeader);


			if (authHeader != null && authHeader.equals("Bearer " + ecomToken)) {
				SecurityContextHolder.getContext().setAuthentication(
						new UsernamePasswordAuthenticationToken("system", null, List.of())
				);
			} else if (userHeader != null) {
				SecurityContextHolder.getContext().setAuthentication(
						new UsernamePasswordAuthenticationToken(userHeader, null, List.of())
				);
			}

			filterChain.doFilter(servletRequest, servletResponse);
		}, UsernamePasswordAuthenticationFilter.class);

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
