package com.project.api_gateway_service.config;

import com.project.common_lib_service.dto.CustomUserPrincipal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationWebFluxFilter implements WebFilter {
    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var headers = request.getHeaders();

        String accountIdHeader = headers.getFirst("X-AccountId");
        String username = headers.getFirst("X-Username");
        String roleHeader = headers.getFirst("X-Role");

        if (accountIdHeader == null || username == null) {
            return chain.filter(exchange);
        }

        List<String> roles = new ArrayList<>();
        if (roleHeader != null && !roleHeader.isBlank()) {
            roles = Arrays.stream(roleHeader.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .collect(Collectors.toList());
        }

        CustomUserPrincipal principal = CustomUserPrincipal.builder()
                .userId(UUID.fromString(accountIdHeader))
                .username(username)
                .roles(roles)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());

        // Đưa thông tin authentication vào ReactiveSecurityContext
        SecurityContextImpl context = new SecurityContextImpl(authentication);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
    }
}
