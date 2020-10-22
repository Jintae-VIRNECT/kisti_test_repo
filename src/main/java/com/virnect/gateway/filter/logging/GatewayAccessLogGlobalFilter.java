package com.virnect.gateway.filter.logging;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
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
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(-1)
public class GatewayAccessLogGlobalFilter implements GlobalFilter {
	static private final String USER_INFO_FORMAT = "[%s,%s,%s]";
	@Value("${jwt.secret}")
	private String secretKey;

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		HttpRequest httpRequest = (HttpRequest)request;

		GatewayAccessLog gatewayAccessLog = new GatewayAccessLog()
			.address(fetchAddressFromRequest(request))
			.port(request.getLocalAddress().getPort())
			.method(httpRequest.method().name())
			.uri(httpRequest.uri())
			.protocol(httpRequest.protocolVersion().text())
			.user(generateUserInfo(request));

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			String responseStatus = String.format("%d %s", response.getRawStatusCode(),
				HttpStatus.valueOf(response.getRawStatusCode()).name()
			);
			gatewayAccessLog
				.status(responseStatus)
				.contentType(response.getHeaders().getContentType().toString())
				.contentLength(response.getHeaders().getContentLength());
			gatewayAccessLog.log();
		}));
	}

	private String fetchJwtTokenFromRequest(ServerHttpRequest request) {
		String bearerToken = request.getHeaders().get("Authorization").get(0);
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
		String jwt = fetchJwtTokenFromRequest(request);
		Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		Claims body = claims.getBody();
		if (body.containsKey("uuid") && body.containsKey("email") && body.containsKey("country")) {
			String uuid = body.get("uuid", String.class);
			String email = body.get("email", String.class);
			String country = body.get("country", String.class);
			return String.format(USER_INFO_FORMAT, uuid, email, country);
		}
		return "-";
	}
}
