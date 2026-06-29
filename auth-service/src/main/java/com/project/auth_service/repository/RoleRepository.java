package com.project.auth_service.repository;

import com.project.auth_service.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query(
            value = """
                    select *
                    from roles r
                    where r.deleted_at is null
                      and (
                            :keyword is null
                            or lower(r.name) like lower(concat('%', cast(:keyword as text), '%'))
                      )
                    """,
            countQuery = """
                    select count(*)
                    from roles r
                    where r.deleted_at is null
                      and (
                            :keyword is null
                            or lower(r.name) like lower(concat('%', cast(:keyword as text), '%'))
                      )
                    """,
            nativeQuery = true
    )
    Page<Role> searchForAdmin(@Param("keyword") String keyword, Pageable pageable);
}
