package com.virnect.gateway.filter.cors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsProcessor;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;

import com.google.common.collect.ImmutableList;

import reactor.core.publisher.Mono;

public class PassCorsRoutePredicateHandlerMapping extends RoutePredicateHandlerMapping {
	private static final WebHandler REQUEST_HANDLED_HANDLER = exchange -> Mono.empty();
	private static final Logger logger = LoggerFactory.getLogger(PassCorsRoutePredicateHandlerMapping.class);
	private final GlobalCorsProperties globalCorsProperties;
	private CorsProcessor corsProcessor = new CorsCustomProcessor();


	public PassCorsRoutePredicateHandlerMapping(
		FilteringWebHandler webHandler,
		RouteLocator routeLocator,
		GlobalCorsProperties globalCorsProperties,
		Environment environment
	) {
		super(webHandler, routeLocator, globalCorsProperties, environment);
		this.globalCorsProperties = globalCorsProperties;
	}

	@Override
	public Mono<Object> getHandler(ServerWebExchange exchange) {
		logger.info("[PassCorsRoutePredicateHandlerMapping] getHandler");
		return getHandlerInternal(exchange).map(handler -> {
			logger.info(exchange.getLogPrefix() + "Mapped to " + handler);
			ServerHttpRequest request = exchange.getRequest();
			String clientIp = request.getHeaders().getFirst("X-Forwarded-For");
			if(clientIp == null){
				clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
			}
			String origin = exchange.getRequest().getHeaders().getOrigin();
			if (hasCorsConfigurationSource(handler) || CorsUtils.isPreFlightRequest(request)) {
				CorsConfiguration corsConfiguration = new CorsConfiguration();
				corsConfiguration.setAllowedOrigins(Arrays.asList(clientIp,origin));
				corsConfiguration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
				corsConfiguration.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
				corsConfiguration.setExposedHeaders(
					ImmutableList.of(HttpHeaders.SET_COOKIE, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
						HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS
					));
				corsConfiguration.setAllowCredentials(true);
				boolean isValid = this.corsProcessor.process(corsConfiguration, exchange);
				if (!isValid || CorsUtils.isPreFlightRequest(request)) {
					return REQUEST_HANDLED_HANDLER;
				}
			}
			return handler;
		});
	}
}
