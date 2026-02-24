package com.nexora.auth.service;

import com.nexora.auth.dto.LoginRequest;
import com.nexora.auth.dto.RegisterRequest;
import com.nexora.auth.entity.User;
import com.nexora.auth.repository.UserRepository;
import com.nexora.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        // 1️⃣ Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // 2️⃣ Create new user
        User user = User.builder()
                //.id(UUID.randomUUID())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        // 3️⃣ Save to database
        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

        // 1️⃣ Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3️⃣ Generate JWT
        return jwtService.generateToken(user.getEmail());
    }
}