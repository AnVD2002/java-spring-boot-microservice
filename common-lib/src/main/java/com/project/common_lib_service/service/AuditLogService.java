package com.project.common_lib_service.service;

public interface AuditLogService {

    void record(Enum<?> action, Enum<?> entityType, Object entityId, Object oldData, Object newData);
}
