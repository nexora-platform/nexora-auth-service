package com.nexora.auth.tenant;

import jakarta.persistence.PrePersist;

import java.util.UUID;

public class TenantEntityListener {

    @PrePersist
    public void setTenant(Object entity) {

        if (entity instanceof TenantAware tenantAware) {

            UUID tenantId = TenantContext.getTenantId();

            if (tenantId != null && tenantAware.getTenantId() == null) {
                tenantAware.setTenantId(tenantId);
            }
        }
    }
}