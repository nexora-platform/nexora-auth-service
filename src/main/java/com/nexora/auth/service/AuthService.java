package com.nexora.auth.service;

import com.nexora.auth.client.TenantClient;
import com.nexora.auth.controller.dto.*;
import com.nexora.auth.entity.User;
import com.nexora.auth.repository.UserRepository;
import com.nexora.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantClient tenantClient;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        log.info("Register request received for email: {} tenant: {}",
                request.getEmail(), request.getTenantId());

        tenantClient.validateTenant(request.getTenantId());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .tenantId(request.getTenantId())
                .build();

        userRepository.save(user);

        log.info("User registered successfully id={} tenant={}",
                user.getId(), user.getTenantId());

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getEnabled(),
                user.getTenantId(),
                user.getCreatedAt()
        );
    }

    public LoginResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed - user not found: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password for user: {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getTenantId(),
                user.getRole().name()
        );

        log.info("User logged in successfully id={} tenant={}",
                user.getId(), user.getTenantId());

        return new LoginResponse(token);
    }
}