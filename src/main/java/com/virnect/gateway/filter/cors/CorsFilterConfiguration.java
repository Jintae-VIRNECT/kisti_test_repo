package com.virnect.gateway.filter.cors;

import javax.annotation.PostConstruct;

import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile(value = {"develop","onpremise"})
public class CorsFilterConfiguration {

	@PostConstruct
	public void init(){
		log.info("[CorsFilterConfiguration] - [PassCorsRoutePredicateHandlerMapping,CorsPassResponseHeaderRewriteFilter] Active");
	}

	@Bean
	@Primary
	public RoutePredicateHandlerMapping passCorsRoutePredicateHandleMapping(
		FilteringWebHandler webHandler, RouteLocator routeLocator,
		GlobalCorsProperties globalCorsProperties, Environment environment
	) {
		return new PassCorsRoutePredicateHandlerMapping(webHandler, routeLocator, globalCorsProperties, environment);
	}

	@Bean
	CorsPassResponseHeaderRewriteFilter CorsPassResponseHeaderRewriteFilter(){
		return new CorsPassResponseHeaderRewriteFilter();
	}
}
