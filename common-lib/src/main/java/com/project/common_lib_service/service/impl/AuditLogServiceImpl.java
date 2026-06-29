package com.project.common_lib_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common_lib_service.dto.CustomUserPrincipal;
import com.project.common_lib_service.entity.AuditLog;
import com.project.common_lib_service.repository.AuditLogRepository;
import com.project.common_lib_service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final ObjectProvider<AuditLogRepository> auditLogRepositoryProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void record(Enum<?> action, Enum<?> entityType, Object entityId, Object oldData, Object newData) {
        AuditLogRepository auditLogRepository = auditLogRepositoryProvider.getIfAvailable();
        if (auditLogRepository == null) {
            throw new IllegalStateException("AuditLogRepository is not configured for this service");
        }
        CustomUserPrincipal actor = getCurrentActor();
        auditLogRepository.save(AuditLog.builder()
                .action(action.name())
                .entityType(entityType.name())
                .entityId(entityId == null ? null : String.valueOf(entityId))
                .actorId(actor == null ? null : actor.getUserId())
                .actorUsername(actor == null ? "SYSTEM" : actor.getUsername())
                .oldData(toJson(oldData))
                .newData(toJson(newData))
                .createdAt(Instant.now())
                .build());
    }

    private CustomUserPrincipal getCurrentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        return principal instanceof CustomUserPrincipal customUserPrincipal ? customUserPrincipal : null;
    }

    private String toJson(Object data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize audit data", e);
        }
    }
}
