package com.project.common_lib_service.config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;



import java.util.Optional;

@Component("myAuditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//            return Optional.empty();
//        }
//
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof CustomUserDetails userDetails) {
//            return Optional.of(userDetails.getId());
//        }
        return Optional.empty();
    }
}
