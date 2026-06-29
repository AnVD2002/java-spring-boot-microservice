package com.project.auth_service.repository;

import com.project.auth_service.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleId(Long roleId);

    void deleteByRoleId(Long roleId);

    @Query("""
            select distinct p.code
            from RolePermission rp
            join Permission p on p.id = rp.permissionId
            where rp.roleId in :roleIds
            """)
    List<String> findPermissionCodesByRoleIds(Collection<Long> roleIds);

    @Query("""
            select p
            from RolePermission rp
            join Permission p on p.id = rp.permissionId
            where rp.roleId = :roleId
            """)
    List<com.project.auth_service.entity.Permission> findPermissionsByRoleId(Long roleId);
}
