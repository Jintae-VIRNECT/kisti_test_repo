package com.virnect.gateway.filter.cors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CorsPassResponseHeaderRewriteFilter implements GlobalFilter {
	private final List<String> ALL = Collections.singletonList("*");

	@PostConstruct
	public void init() {
		log.info("[CorsPassResponseHeaderRewriteFilter] => Active");
	}

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {
		return chain.filter(exchange)
			.then(Mono.fromRunnable(() -> rewriteHeaders(exchange)));
	}

	protected void rewriteHeaders(ServerWebExchange exchange) {
		final HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
		final String origin = exchange.getRequest().getHeaders().getOrigin();
		responseHeaders.setAccessControlAllowOrigin(origin);
		responseHeaders.setAccessControlExposeHeaders(
			ImmutableList.of(
				HttpHeaders.SET_COOKIE, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
				HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_DISPOSITION,
				HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS
			));
		responseHeaders.setAccessControlAllowHeaders(ALL);
		responseHeaders.setAccessControlAllowMethods(Arrays.asList(HttpMethod.values()));
		responseHeaders.setAccessControlAllowCredentials(true);
	}

	protected String fetchAddressFromRequest(ServerHttpRequest request) {
		String clientIp = request.getHeaders().getFirst("X-Forwarded-For");

		if (clientIp != null) {
			return clientIp;
		}

		if (request.getRemoteAddress() != null) {
			return request.getRemoteAddress().getAddress().getHostAddress();
		}

		return "-";
	}
}
