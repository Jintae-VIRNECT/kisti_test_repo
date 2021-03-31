package com.virnect.gateway.filter.security.jwt;

import java.util.Base64;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
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

import com.virnect.gateway.error.ErrorCode;
import com.virnect.gateway.filter.security.GatewayServerAuthenticationException;
import com.virnect.gateway.filter.security.RequestValidationProcessor;

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
@Order(10)
public class JwtAuthenticationFilter implements GlobalFilter {
	private final static Logger logger = Loggers.getLogger(
		"com.virnect.gateway.filter.security.jwt.JwtAuthenticationFilter");
	@Value("${jwt.secret}")
	private String secretKey;
	@Value("${spring.profiles.active}")
	private String activeProfile;

	@PostConstruct
	protected void init() {
		log.info("[JWT Authentication Filter] - Active.");
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("JwtAuthenticationFilter - doFilter");

		if (RequestValidationProcessor.isRequestAuthenticationProcessSkip(exchange.getRequest())) {
			log.info("JwtAuthenticationFilter - Skip swagger json request like [/v2/api-docs] ");
			return chain.filter(exchange);
		}

		// if develop and onpremise environment and request is swagger ui related.
		if ((activeProfile.equals("develop") || activeProfile.equals("onpremise")) &&
			RequestValidationProcessor.isSwaggerApiDocsUrl(exchange.getRequest())
		) {
			log.info("JwtAuthenticationFilter - Skip swagger json request like [/v2/api-docs] ");
			return chain.filter(exchange);
		}

		// Check Request Authentication Type
		if (RequestValidationProcessor.isSessionAuthenticationRequest(exchange.getRequest())) {
			log.info("JwtAuthenticationFilter - Find session cookie on request. Ignore jwt authentication process.");
			return chain.filter(exchange);
		}

		// if not Jwt Authentication Request
		if (!RequestValidationProcessor.isJwtAuthenticationRequest(exchange.getRequest())) {
			log.error("JwtAuthenticationFilter - No Session Cookie, No Authentication Token Request. It will Block.");
			throw new GatewayServerAuthenticationException(ErrorCode.ERR_API_AUTHENTICATION);
		}

		Optional<String> jwtInfo = Optional.ofNullable(getJwtTokenFromRequest(exchange.getRequest()));

		if (!jwtInfo.isPresent()) {
			log.info("JwtAuthenticationFilter - Can't find Jwt on request Authorization Header");
			log.error("JwtAuthenticationFilter - No Session Cookie, No Authentication Token Request. It will Block.");
			throw new GatewayServerAuthenticationException(ErrorCode.ERR_API_AUTHENTICATION);
		}

		String jwt = jwtInfo.get();
		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		Claims body = claims.getBody();

		logger.info("JwtAuthenticationFilter - [AUTHENTICATION TOKEN]: {}", body.toString());

		ServerHttpRequest authenticateRequest = exchange.getRequest().mutate()
			.header("X-jwt-uuid", body.get("uuid", String.class))
			.header("X-jwt-email", body.get("email", String.class))
			.header("X-jwt-name", Base64.getEncoder().encodeToString(body.get("name", String.class).getBytes()))
			.header("X-jwt-ip", body.get("ip", String.class))
			.header("X-jwt-country", body.get("country", String.class))
			.header("X-jwt-countryCode", body.get("countryCode", String.class))
			.header("X-jwt-jwtId", body.get("jwtId", String.class))
			.build();

		return chain.filter(exchange.mutate().request(authenticateRequest).build());
	}

	private String getJwtTokenFromRequest(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			int tokenSize = bearerToken.length();
			return bearerToken.substring(7, tokenSize);
		}
		return null;
	}
}
