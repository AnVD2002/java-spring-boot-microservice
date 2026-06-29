package com.project.auth_service.repository;

import com.project.auth_service.entity.ScreenPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScreenPermissionRepository extends JpaRepository<ScreenPermission, UUID> {

    List<ScreenPermission> findByScreenId(Long screenId);

    void deleteByScreenId(Long screenId);
}
