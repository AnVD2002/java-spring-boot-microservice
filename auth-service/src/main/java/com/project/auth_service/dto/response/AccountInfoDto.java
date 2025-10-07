package com.project.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDto {
    private UUID id;
    private String username;
    private String password;
    private String role;
    private String email;
}
