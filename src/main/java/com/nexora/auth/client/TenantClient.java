package com.nexora.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "nexora-tenant-service")
public interface TenantClient {

    @GetMapping("/api/v1/tenants/{id}")
    void validateTenant(@PathVariable UUID id);
}