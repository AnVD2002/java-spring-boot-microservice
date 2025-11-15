package com.project.auth_service.entity;

import com.project.common_lib_service.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "device_log")
public class DeviceLog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "first_seen_at")
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "status")
    private Integer status;

}
