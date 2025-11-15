package com.project.common_lib_service.aop;

import com.project.common_lib_service.utils.Secured;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
public class SecurityAspect {

    @Before("@annotation(secured)")
    public void checkPermission(JoinPoint joinPoint, Secured secured) {
        // Get Authentication from Reactive Security Context
        Authentication auth = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .block();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Access denied: User not authenticated");
        }

        // Get ServerWebExchange from method arguments
        ServerWebExchange exchange = findExchange(joinPoint);
        if (exchange == null) {
            throw new IllegalStateException("ServerWebExchange not found in method parameters");
        }

        // Get X-Role header
        String role = exchange.getRequest().getHeaders().getFirst("X-Role");

        boolean hasRole = Arrays.stream(secured.roles())
                .anyMatch(roleValid -> Objects.equals(role, roleValid.name()));

        if (!hasRole) {
            throw new AccessDeniedException("Access denied: User does not have required role");
        }
    }

    private ServerWebExchange findExchange(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ServerWebExchange) {
                return (ServerWebExchange) arg;
            }
        }
        return null;
    }
}
