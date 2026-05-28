package com.project.common_lib_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
    @CreatedDate
    @Column(name = "inserted_at", updatable = false)
    private Instant insertedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deleteAt;

    @CreatedBy
    @Column(name = "inserted_by", updatable = false)
    private UUID insertedBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updateBy;

    @Column(name = "deleted_by")
    private UUID deleteBy;
}
