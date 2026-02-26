package com.nexora.auth.controller.dto;

import com.nexora.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegisterResponse {

    private UUID id;
    private String email;
    private Role role;
    private Boolean enabled;
    private UUID tenantId;
    private LocalDateTime createdAt;
}