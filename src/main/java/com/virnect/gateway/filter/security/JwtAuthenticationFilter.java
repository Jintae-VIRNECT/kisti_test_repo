package com.virnect.gateway.filter.security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.27
 */

@Slf4j
@Profile(value = {"develop", "staging", "production"})
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {
	@Value("${jwt.secret}")
	private String secretKey;

	@PostConstruct
	protected void init() {
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestUrlPath = exchange.getRequest().getURI().getPath();
		boolean isAuthenticateSkipUrl = requestUrlPath.startsWith("/auth") ||
			requestUrlPath.startsWith("/admin") ||
			requestUrlPath.startsWith("/users/find") ||
			requestUrlPath.startsWith("/licenses/allocate/check") ||
			requestUrlPath.startsWith("/licenses/allocate") ||
			requestUrlPath.startsWith("/licenses/deallocate") ||
			requestUrlPath.matches("^/workspaces/([a-zA-Z0-9]+)/invite/accept$");

		if (isAuthenticateSkipUrl) {
			return chain.filter(exchange);
		}
		String jwt = Optional.ofNullable(getJwtTokenFromRequest(exchange.getRequest()))
			.orElseThrow(() -> new MalformedJwtException("JWT Token not exist"));
		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		Claims body = claims.getBody();
		log.info("[AUTHENTICATION TOKEN] : [{}]", body.toString());
		ServerHttpRequest authenticateRequest = exchange.getRequest().mutate()
			.header("X-jwt-uuid", body.get("uuid", String.class))
			.header("X-jwt-email", body.get("email", String.class))
			.header("X-jwt-name", Base64.getEncoder().encodeToString(body.get("name", String.class).getBytes()))
			.header("X-jwt-ip", body.get("ip", String.class))
			.header("X-jwt-country", body.get("country", String.class))
			.header("X-jwt-countryCode", body.get("countryCode", String.class))
			.header("X-jwt-jwtId", body.get("jwtId", String.class))
			.build();

		authenticateRequest.getHeaders()
			.entrySet()
			.forEach((entry -> log.info("[AUTHENTICATE_REQUEST] [HEADER] [{}] => {} ", entry.getKey(),
				Arrays.toString(entry.getValue().toArray())
			)));

		// MDC Request User Information Logging
		MDC.put("email", body.get("email", String.class));
		MDC.put("uuid", body.get("uuid", String.class));
		MDC.put("ip", body.get("ip", String.class));
		MDC.put("country", body.get("country", String.class));
		MDC.put("countryCode", body.get("countryCode", String.class));

		return chain.filter(exchange.mutate().request(authenticateRequest).build());
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
