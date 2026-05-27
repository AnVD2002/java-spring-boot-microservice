package com.project.common_lib_service.config;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("myAuditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserPrincipal customPrincipal) {
            return Optional.ofNullable(customPrincipal.getUsername());
        }
        if (principal instanceof String strPrincipal && !"anonymousUser".equals(strPrincipal)) {
            return Optional.of(strPrincipal);
        }
        return Optional.empty();
    }
}
