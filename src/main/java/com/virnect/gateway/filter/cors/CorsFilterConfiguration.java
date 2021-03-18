package com.virnect.gateway.filter.cors;

import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class CorsFilterConfiguration {

	@Bean
	@Primary
	public RoutePredicateHandlerMapping passCorsRoutePredicateHandleMapping(
		FilteringWebHandler webHandler, RouteLocator routeLocator,
		GlobalCorsProperties globalCorsProperties, Environment environment
	) {
		return new PassCorsRoutePredicateHandlerMapping(webHandler, routeLocator, globalCorsProperties, environment);
	}
}
