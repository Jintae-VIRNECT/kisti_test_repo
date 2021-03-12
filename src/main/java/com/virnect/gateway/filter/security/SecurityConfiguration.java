package com.virnect.gateway.filter.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		// CRSF Security Configuration
		http.httpBasic().disable();
		http.cors().disable();
		http.logout().disable();
		http.formLogin().disable();
		http.csrf().disable();
		http.authorizeExchange().anyExchange().permitAll();
		return http.build();
	}
}
