package com.nexora.auth.service;

import com.nexora.auth.controller.dto.*;
import com.nexora.auth.entity.User;
import com.nexora.auth.repository.UserRepository;
import com.nexora.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getTenantId(),
                user.getRole().name()
        );

        return new LoginResponse(token);
    }
}