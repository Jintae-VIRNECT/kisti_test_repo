package com.virnect.gateway.filter.security.session;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.session.CookieWebSessionIdResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.RequiredArgsConstructor;

import com.virnect.security.UserAuthenticationDetails;
import com.virnect.security.UserAuthenticationDetailsMixin;

@Configuration
@EnableRedisWebSession
@RequiredArgsConstructor
public class SessionConfiguration implements BeanClassLoaderAware {
	private final SessionCookieProperty sessionCookieProperty;
	private ClassLoader classLoader;

	@Bean
	public CookieWebSessionIdResolver customCookieWebSessionIdResolver() {
		CookieWebSessionIdResolver cookieWebSessionIdResolver = new CookieWebSessionIdResolver();
		cookieWebSessionIdResolver.setCookieName(sessionCookieProperty.getName());
		return cookieWebSessionIdResolver;
	}

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModules(SecurityJackson2Modules.getModules(this.classLoader));
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.setMixInAnnotation(UserAuthenticationDetails.class, UserAuthenticationDetailsMixin.class);
		objectMapper.registerModule(simpleModule);

		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
}
