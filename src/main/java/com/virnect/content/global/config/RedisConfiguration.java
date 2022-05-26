package com.virnect.content.global.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfiguration {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@PostConstruct
	public void init() {
		log.info("[REDIS_CONFIG] Host >> [{}]", host);
		log.info("[REDIS_CONFIG] Port >> [{}]", port);
	}
}
