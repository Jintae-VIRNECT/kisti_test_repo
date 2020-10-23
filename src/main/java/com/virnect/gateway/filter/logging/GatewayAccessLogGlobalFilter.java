package com.virnect.gateway.filter.logging;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-4)
public class GatewayAccessLogGlobalFilter implements GlobalFilter, Ordered {
	static private final String USER_INFO_FORMAT = "[%s,%s,%s]";
	@Value("${jwt.secret}")
	private String secretKey;

	@PostConstruct
	protected void init() {
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		GatewayAccessLog gatewayAccessLog = new GatewayAccessLog()
			.address(fetchAddressFromRequest(request))
			.port(request.getLocalAddress().getPort())
			.method(request.getMethod().name())
			.uri(request.getURI().toString())
			.protocol("protocol")
			.user(generateUserInfo(request));

		if (request.getHeaders().get("user-agent") != null && request.getHeaders().get("user-agent").size() > 0) {
			gatewayAccessLog.userAgent(request.getHeaders().get("user-agent").get(0));
		}

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			String responseStatus = String.format("%d %s", response.getRawStatusCode(),
				HttpStatus.valueOf(response.getRawStatusCode()).name()
			);
			gatewayAccessLog
				.status(responseStatus);

			if (!request.getURI().toString().contains("ws") && !request.getURI().toString().contains("wss")) {
				gatewayAccessLog.contentType(response.getHeaders().getContentType().toString())
					.contentLength(response.getHeaders().getContentLength());
			}

			gatewayAccessLog.log();
		}));
	}

	private String fetchJwtTokenFromRequest(ServerHttpRequest request) {
		List<String> bearerTokenList = Optional.ofNullable(request.getHeaders().get("Authorization"))
			.orElse(new ArrayList<>());
		if (bearerTokenList.isEmpty()) {
			return null;
		}
		String bearerToken = bearerTokenList.get(0);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			int tokenSize = bearerToken.length();
			return bearerToken.substring(7, tokenSize);
		}
		return null;
	}

	private String fetchAddressFromRequest(ServerHttpRequest request) {
		return Optional.ofNullable(
			request.getHeaders().getFirst("X-Forwarded-For")
		).orElse(request.getRemoteAddress().getHostName());
	}

	private String generateUserInfo(ServerHttpRequest request) {
		try {
			String jwt = fetchJwtTokenFromRequest(request);
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
			Claims body = claims.getBody();
			if (body.containsKey("uuid") && body.containsKey("email") && body.containsKey("country")) {
				String uuid = body.get("uuid", String.class);
				String email = body.get("email", String.class);
				String country = body.get("country", String.class);
				return String.format(USER_INFO_FORMAT, uuid, email, country);
			}
		} catch (Exception e) {
			log.info("{}", e.getMessage());
		}
		return String.format(USER_INFO_FORMAT, "-", "-", "-");
	}

	@Override
	public int getOrder() {
		return -5;
	}
}
