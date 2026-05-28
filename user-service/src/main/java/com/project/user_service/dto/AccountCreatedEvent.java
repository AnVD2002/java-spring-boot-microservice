package com.project.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent {
    private String sagaId;
    private UUID accountId;
    private String email;
    private String username;
}
