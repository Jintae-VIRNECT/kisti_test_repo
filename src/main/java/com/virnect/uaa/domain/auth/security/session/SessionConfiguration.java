package com.virnect.uaa.domain.auth.security.session;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.security.UserAuthenticationDetails;
import com.virnect.security.UserAuthenticationDetailsMixin;

@Slf4j
@Configuration
@EnableRedisHttpSession
@RequiredArgsConstructor
public class SessionConfiguration<S extends Session> {
	private final SessionCookieProperty sessionCookieProperty;
	private final ApplicationEventPublisher applicationEventPublisher;

	@PostConstruct
	public void init() {
		log.info("Session Cookie - Configuration Properties: [{}]", sessionCookieProperty);
	}

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName(sessionCookieProperty.getName());
		serializer.setCookiePath(sessionCookieProperty.getPath());

		if (sessionCookieProperty.isDomainEnabled()) {
			log.info("Session Cookie Domain Set Enabled.");
			if (sessionCookieProperty.hasDomainNamePattern()) {
				serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
				log.info("Session Cookie - Domain Pattern apply ! ");
			} else {
				log.info("Session Cookie - Domain Name apply ! ");
				serializer.setDomainName(sessionCookieProperty.getDomain());
			}
		}

		serializer.setUseSecureCookie(sessionCookieProperty.isSecureCookieEnabled());
		serializer.setUseHttpOnlyCookie(sessionCookieProperty.isHttpOnlyCookieEnabled());
		serializer.setSameSite(sessionCookieProperty.getSameSite());
		serializer.setUseBase64Encoding(false);
		return serializer;
	}

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new CoreJackson2Module());
		objectMapper.registerModules(SecurityJackson2Modules.getModules(this.getClass().getClassLoader()));
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.setMixInAnnotation(UserAuthenticationDetails.class, UserAuthenticationDetailsMixin.class);
		objectMapper.registerModule(simpleModule);
		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	@Bean
	public SpringSessionBackedSessionRegistry<S> springSessionBackendRegistry(
		FindByIndexNameSessionRepository redisSessionRegistry
	) {
		return new SpringSessionBackedSessionRegistry<S>(redisSessionRegistry);
	}

	@Bean
	public CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy(
		SpringSessionBackedSessionRegistry springSessionBackendRegistry,
		FindByIndexNameSessionRepository sessionRepository
	) {
		List<SessionAuthenticationStrategy> delegateSessionStrategies = new ArrayList<>();
		delegateSessionStrategies.add(customSessionControlAuthenticationStrategy(springSessionBackendRegistry));
		delegateSessionStrategies.add(new SessionFixationProtectionStrategy());
		delegateSessionStrategies.add(
			new RegisterSessionAuthenticationStrategy(springSessionBackendRegistry(sessionRepository)));
		return new CompositeSessionAuthenticationStrategy(delegateSessionStrategies);
	}

	@Bean
	public CustomSessionControlAuthenticationStrategy<S> customSessionControlAuthenticationStrategy(
		SpringSessionBackedSessionRegistry sessionRegistry
	) {
		CustomSessionControlAuthenticationStrategy<S> sessionControlAuthenticationStrategy = new CustomSessionControlAuthenticationStrategy<>(
			sessionRegistry, applicationEventPublisher);
		sessionControlAuthenticationStrategy.setMaximumSessions(1);
		sessionControlAuthenticationStrategy.setExceptionIfMaximumExceeded(true);
		return sessionControlAuthenticationStrategy;
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}


	@Bean
	ConfigureRedisAction configureRedisAction() {

		return ConfigureRedisAction.NO_OP;
	}
}
