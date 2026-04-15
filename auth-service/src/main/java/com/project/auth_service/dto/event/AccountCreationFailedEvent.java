package com.project.auth_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreationFailedEvent {
    private String sagaId;
    private UUID accountId;
    private String reason;
}
