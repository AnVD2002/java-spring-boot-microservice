package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "blacklist_tokens")
public class BlacklistToken extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blacklist_token_seq")
    @SequenceGenerator(name = "blacklist_token_seq", sequenceName = "blacklist_token_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "access_token", unique = true, nullable = false, length = 512)
    private String accessToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reason", nullable = false)
    private String reason;
}
