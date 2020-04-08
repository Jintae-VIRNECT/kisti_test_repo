package com.virnect.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@SpringBootApplication
@EnableEurekaClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
////                .route(p -> p
////                        .path("/auth/**")
////                        .filters(f-> f.dedupeResponseHeader("Access-Control-Allow-Origin:=",DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_FIRST.name()))
////                        .uri("lb://auth-server")
////                        .id("auth-server"))
//                .route(p -> p
//                        .path("/users/**")
//                        .uri("lb://user-server")
//                        .id("user-server"))
//                .route(p -> p.path("/workspace/**")
//                        .uri("lb://workspace-server")
//                        .id("workspace-server"))
//                .route(p -> p.path("/admin/**")
//                        .uri("lb://admin-server")
//                        .id("admin-server"))
//                .route(p -> p.path("/contents/**")
//                        .uri("lb://content-server")
//                        .id("content-server"))
//                .route(p -> p.path("/processes/**")
//                        .uri("lb://process-server")
//                        .id("process-server"))
//                .build();
//    }
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(false);
//        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
//        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
//        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
//        corsConfiguration.addExposedHeader(HttpHeaders.SET_COOKIE);
//        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsWebFilter(corsConfigurationSource);
//    }
}
