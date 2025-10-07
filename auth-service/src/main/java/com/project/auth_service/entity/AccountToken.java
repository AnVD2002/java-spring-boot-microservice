package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_tokens")
public class AccountToken extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_token_seq")
    @SequenceGenerator(name = "account_token_seq", sequenceName = "account_token_id_seq", allocationSize = 1)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoke", nullable = false)
    private Boolean isRevoked;

    @Column(name = "provider", nullable = false)
    private String provider;
}
