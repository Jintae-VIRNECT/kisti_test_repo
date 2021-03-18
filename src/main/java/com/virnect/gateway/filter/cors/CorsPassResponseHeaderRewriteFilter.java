package com.virnect.gateway.filter.cors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CorsPassResponseHeaderRewriteFilter implements GlobalFilter {
	private final List<String> ALL = Collections.singletonList("*");

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
		final String host = fetchAddressFromRequest(exchange.getRequest());
		responseHeaders.setAccessControlAllowOrigin(origin);
		responseHeaders.setAccessControlExposeHeaders(Collections.singletonList(HttpHeaders.SET_COOKIE));
		responseHeaders.setAccessControlAllowHeaders(ALL);
		responseHeaders.setAccessControlAllowMethods(Arrays.asList(HttpMethod.values()));
		responseHeaders.setAccessControlAllowCredentials(true);
		responseHeaders.computeIfPresent(HttpHeaders.SET_COOKIE, (k, v) -> rewriteCookieDomain(v, origin));

		log.info("responseHeader Rewrite : " + responseHeaders.values());
	}

	private List<String> rewriteCookieDomain(List<String> v, String changeOrigin) {
		return v.stream().map( s -> {
			if(s.contains("SESSION")){
				log.info("ResponseCookie: [{}]", s);
				log.info("Replaced ResponseCookie: [{}]", s.replaceAll("localhost", changeOrigin));
				return s.replaceAll("localhost", changeOrigin);
			}
			return s;
		}).collect(Collectors.toList());
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
