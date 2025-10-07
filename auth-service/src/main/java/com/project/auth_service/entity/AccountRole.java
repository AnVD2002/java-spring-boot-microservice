package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_roles")
public class AccountRole extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_role_seq")
    @SequenceGenerator(name = "account_role_seq", sequenceName = "account_role_id_seq", allocationSize = 1)
    @Column(name = "id")
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;
}
