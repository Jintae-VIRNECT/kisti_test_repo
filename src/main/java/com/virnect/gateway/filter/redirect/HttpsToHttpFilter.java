package com.virnect.gateway.filter.redirect;

import java.net.URI;
import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.24
 */

@Component
@Profile({"local", "develop"})
public class HttpsToHttpFilter implements GlobalFilter, Ordered {
	private static final int HTTPS_TO_HTTP_FILTER_ORDER = 10099;
	private final static Logger logger = Loggers.getLogger("com.virnect.gateway.filter.redirect.HttpsToHttpFilter");

	@PostConstruct
	protected void init() {
		logger.info("[HTTPS TO HTTP FILTER] => ACTIVE");
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI originalUri = exchange.getRequest().getURI();
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpRequest.Builder mutate = request.mutate();
		String forwardedUri = request.getURI().toString();

		if (request.getURI().toString().startsWith("/media") || request.getURI().toString().contains("/record")) {
			return chain.filter(exchange);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("[HTTPS REDIRECT TO HTTP]: ").append("[").append(forwardedUri).append("] => ");
		if (forwardedUri != null && forwardedUri.startsWith("https")) {
			try {
				URI mutatedUri = new URI(
					"http",
					originalUri.getUserInfo(),
					originalUri.getHost(),
					originalUri.getPort(),
					originalUri.getPath(),
					originalUri.getQuery(),
					originalUri.getFragment()
				);
				mutate.uri(mutatedUri);
				sb.append("[")
					.append(mutatedUri.getScheme())
					.append("://")
					.append(mutatedUri.getHost())
					.append(":")
					.append(mutatedUri.getPort())
					.append(mutatedUri.getPath())
					.append("]");
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		ServerHttpRequest build = mutate.build();
		logger.info("{}", sb.toString());
		return chain.filter(exchange.mutate().request(build).build());
	}

	@Override
	public int getOrder() {
		return HTTPS_TO_HTTP_FILTER_ORDER;
	}
}
