package com.aryan.userservice.service.auth;

import com.aryan.userservice.dto.OrderRequest;
import com.aryan.userservice.dto.SignupRequest;
import com.aryan.userservice.dto.UserDto;
import com.aryan.userservice.enums.OrderStatus;
import com.aryan.userservice.enums.UserRole;
import com.aryan.userservice.model.User;
import com.aryan.userservice.repository.UserRepository;
import com.aryan.userservice.feign.OrderClient;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder bEncoder;

    @Autowired
    private final OrderClient orderClient;

    public UserDto createUser(SignupRequest signupRequest) {
        log.info("Creating user with email: {}", signupRequest.getEmail());

        User createdUser = userRepository.save(User.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(bEncoder.encode(signupRequest.getPassword()))
                .role(UserRole.CUSTOMER)
                .build());


        OrderRequest orderRequest = OrderRequest.builder()
                .userId(createdUser.getId())
                .amount(0L)
                .totalAmount(0L)
                .discount(0L)
                .orderStatus(OrderStatus.Pending)
                .build();

        orderClient.createOrder(orderRequest);


        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());
        userDto.setEmail(createdUser.getEmail());
        userDto.setName(createdUser.getName());
        userDto.setUserRole(createdUser.getRole());

        return userDto;


    }

    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @PostConstruct
    public void createAdminAccount() {
        log.info("Running application for the first time creates an Admin account with default info");
        Optional<User> adminAccountUser = userRepository.findByRole(UserRole.ADMIN);
        if (adminAccountUser.isEmpty()) {
            log.info("Admin account created with email: admin@gmail.com and password: admin");
            userRepository.save(
                    User.builder()
                            .email("admin@gmail.com")
                            .name("admin")
                            .role(UserRole.ADMIN)
                            .password(bEncoder.encode("admin"))
                            .build()
            );
        }
    }

}
