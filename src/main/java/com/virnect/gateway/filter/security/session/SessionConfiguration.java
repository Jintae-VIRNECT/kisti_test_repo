package com.virnect.gateway.filter.security.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.session.CookieWebSessionIdResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableRedisWebSession
@RequiredArgsConstructor
public class SessionConfiguration {
	private final SessionCookieProperty sessionCookieProperty;

	@Bean
	public CookieWebSessionIdResolver customCookieWebSessionIdResolver() {
		CookieWebSessionIdResolver cookieWebSessionIdResolver = new CookieWebSessionIdResolver();
		cookieWebSessionIdResolver.setCookieName(sessionCookieProperty.getName());
		return cookieWebSessionIdResolver;
	}

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CoreJackson2Module());
		objectMapper.registerModules(SecurityJackson2Modules.getModules(this.getClass().getClassLoader()));
		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}
}
