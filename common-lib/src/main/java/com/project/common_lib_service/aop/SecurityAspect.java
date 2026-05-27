package com.project.common_lib_service.aop;

import com.project.common_lib_service.utils.RoleEnum;
import com.project.common_lib_service.utils.Secured;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class SecurityAspect {

    @Before("@annotation(secured)")
    public void checkPermission(JoinPoint joinPoint, Secured secured) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Access denied: User not authenticated");
        }

        boolean hasRole = Arrays.stream(secured.roles())
                .map(RoleEnum::name)
                .anyMatch(role -> auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + role)));

        if (!hasRole) {
            throw new AccessDeniedException("Access denied: insufficient role");
        }
    }
}
