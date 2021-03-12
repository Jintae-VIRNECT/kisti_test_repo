package com.virnect.gateway.filter.security.jwt;

import java.util.Base64;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import com.virnect.gateway.filter.security.session.SessionCookieProperty;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.27
 */

@Slf4j
@Component
@RequiredArgsConstructor
@Order(11)
@Profile(value = {"staging", "production"})
public class JwtAuthenticationFilter implements GlobalFilter {
	private final static Logger logger = Loggers.getLogger(
		"com.virnect.gateway.filter.security.jwt.JwtAuthenticationFilter");
	private final SessionCookieProperty sessionCookieProperty;
	@Value("${jwt.secret}")
	private String secretKey;

	@PostConstruct
	protected void init() {
		logger.info("[JWT AUTHENTICATION FILTER] => ACTIVE");
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String requestUrlPath = exchange.getRequest().getURI().getPath();
		boolean isAuthenticateSkipUrl = requestUrlPath.startsWith("/auth") ||
			requestUrlPath.startsWith("/v1/auth") ||
			requestUrlPath.startsWith("/admin") ||
			requestUrlPath.startsWith("/users/find") ||
			requestUrlPath.startsWith("/licenses/allocate/check") ||
			requestUrlPath.startsWith("/licenses/allocate") ||
			requestUrlPath.startsWith("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/sdk/authentication") ||
			requestUrlPath.matches("^/workspaces/([a-zA-Z0-9]+)/invite/accept$") ||
			requestUrlPath.matches("^/workspaces/invite/[a-zA-Z0-9]+/(accept|reject).*$");

		if (isAuthenticateSkipUrl) {
			return chain.filter(exchange);
		}

		// Check Request Authentication Type
		if (isSessionCookieRequest(exchange.getRequest())) {
			chain.filter(exchange);
		}

		logger.info("JWT Authentication Filter Start");
		Optional<String> jwtInfo = Optional.ofNullable(getJwtTokenFromRequest(exchange.getRequest()));

		if (!jwtInfo.isPresent()) {
			log.info("Skip Jwt Authentication Filter.....");
			log.info("Do Next Session Cookie Authentication Filter.....");
			chain.filter(exchange);
		}

		String jwt = jwtInfo.get();
		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		Claims body = claims.getBody();

		logger.info("[AUTHENTICATION TOKEN]: {}", body.toString());

		ServerHttpRequest authenticateRequest = exchange.getRequest().mutate()
			.header("X-jwt-uuid", body.get("uuid", String.class))
			.header("X-jwt-email", body.get("email", String.class))
			.header("X-jwt-name", Base64.getEncoder().encodeToString(body.get("name", String.class).getBytes()))
			.header("X-jwt-ip", body.get("ip", String.class))
			.header("X-jwt-country", body.get("country", String.class))
			.header("X-jwt-countryCode", body.get("countryCode", String.class))
			.header("X-jwt-jwtId", body.get("jwtId", String.class))
			.build();

		return chain.filter(exchange.mutate().request(authenticateRequest).build())
			.then(Mono.fromRunnable(() -> logger.info("JWT Authentication Filter end")));
	}

	private String getJwtTokenFromRequest(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			int tokenSize = bearerToken.length();
			return bearerToken.substring(7, tokenSize);
		}
		return null;
	}

	private boolean isSessionCookieRequest(ServerHttpRequest request) {
		return request.getCookies().containsKey(sessionCookieProperty.getName());
	}
}
