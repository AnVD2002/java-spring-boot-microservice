package com.project.auth_service.repository;

import com.project.auth_service.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    Optional<Screen> findByCode(String code);

    Optional<Screen> findByRoutePath(String routePath);

    @Query("""
            select distinct s
            from Screen s
            join ScreenPermission sp on sp.screenId = s.id
            join Permission p on p.id = sp.permissionId
            where p.code in :permissionCodes
              and s.visible = true
            order by s.displayOrder asc, s.name asc
            """)
    List<Screen> findVisibleScreensByPermissionCodes(Collection<String> permissionCodes);
}
