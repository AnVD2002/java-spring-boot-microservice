package com.project.auth_service.repository;

import com.project.auth_service.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);

    @Query(
            value = """
                    select *
                    from permissions p
                    where p.deleted_at is null
                      and (:module is null or p.module = :module)
                      and (:scope is null or p.scope = :scope)
                      and (
                            :keyword is null
                            or lower(p.code) like lower(concat('%', cast(:keyword as text), '%'))
                            or lower(p.name) like lower(concat('%', cast(:keyword as text), '%'))
                      )
                    """,
            countQuery = """
                    select count(*)
                    from permissions p
                    where p.deleted_at is null
                      and (:module is null or p.module = :module)
                      and (:scope is null or p.scope = :scope)
                      and (
                            :keyword is null
                            or lower(p.code) like lower(concat('%', cast(:keyword as text), '%'))
                            or lower(p.name) like lower(concat('%', cast(:keyword as text), '%'))
                      )
                    """,
            nativeQuery = true
    )
    Page<Permission> searchForAdmin(@Param("keyword") String keyword,
                                    @Param("module") String module,
                                    @Param("scope") String scope,
                                    Pageable pageable);
}
