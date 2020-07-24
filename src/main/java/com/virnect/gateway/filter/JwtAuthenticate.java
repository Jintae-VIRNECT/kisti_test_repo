package com.virnect.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Optional;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.27
 */

@Slf4j
@Profile(value = {"staging", "production"})
@Component
@RequiredArgsConstructor
public class JwtAuthenticate implements GlobalFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUriPath = exchange.getRequest().getURI().getPath();
        boolean isAuthenticateSkipUrl = requestUriPath.startsWith("/auth") ||
                requestUriPath.startsWith("/admin") ||
                requestUriPath.startsWith("/users/find") ||
                requestUriPath.startsWith("/licenses/allocate/check") ||
                requestUriPath.startsWith("/licenses/allocate") ||
                requestUriPath.startsWith("/licenses/deallocate") ||
                requestUriPath.matches("^/workspaces/([a-zA-Z0-9]+)/invite/accept$");

        if (isAuthenticateSkipUrl) {
            return chain.filter(exchange);
        }
        String jwt = Optional.ofNullable(getJwtTokenFromRequest(exchange.getRequest()))
                .orElseThrow(() -> new MalformedJwtException("JWT Token not exist"));
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        log.info("[AUTHENTICATION TOKEN] : [{}]", claims.getBody().toString());
        return chain.filter(exchange);
    }


    private String getJwtTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().get("Authorization").get(0);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            int tokenSize = bearerToken.length();
            return bearerToken.substring(7, tokenSize);
        }
        return null;
    }
}
