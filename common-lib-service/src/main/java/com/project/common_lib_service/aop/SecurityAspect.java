package com.project.common_lib_service.aop;

import com.project.common_lib_service.utils.RoleEnum;
import com.project.common_lib_service.utils.Secured;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final HttpServletRequest request;

    @Before("@annotation(secured)")
    public void checkPermission(Secured secured) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Access denied: User not authenticated");
        }

        String role = request.getHeader("X-Role");
        boolean hasRole = false;
        for (RoleEnum roleValid : secured.roles()) {
            if (Objects.equals(role, roleValid.name())) {
                hasRole = true;
                break;
            }
        }
        if (!hasRole) {
            throw new AccessDeniedException("Access denied: User dose not have required role");
        }
    }
}
