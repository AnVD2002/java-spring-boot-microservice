package com.project.user_service.repository;

import com.project.user_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByAccountId(UUID accountId);

    Optional<User> findByAccountId(UUID accountId);

    @Query(
            value = """
                    select u
                    from User u
                    where u.deleteAt is null
                      and (:status is null or u.status = :status)
                      and (
                            lower(u.email) like concat('%', :keyword, '%')
                            or lower(u.username) like concat('%', :keyword, '%')
                            or lower(u.firstName) like concat('%', :keyword, '%')
                            or lower(u.lastName) like concat('%', :keyword, '%')
                      )
                    """,
            countQuery = """
                    select count(u)
                    from User u
                    where u.deleteAt is null
                      and (:status is null or u.status = :status)
                      and (
                            lower(u.email) like concat('%', :keyword, '%')
                            or lower(u.username) like concat('%', :keyword, '%')
                            or lower(u.firstName) like concat('%', :keyword, '%')
                            or lower(u.lastName) like concat('%', :keyword, '%')
                      )
                    """
    )
    Page<User> searchForAdmin(@Param("keyword") String keyword,
                              @Param("status") Integer status,
                              Pageable pageable);

}
