package com.virnect.uaa.global.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import com.virnect.user.dto.response.UserInfoResponse;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfiguration {
	private final LettuceConnectionFactory lettuceConnectionFactory;
	private final ObjectMapper objectMapper;

	@Bean
	public CacheManager cacheManager() {
		// todo: cache all dto types
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
			UserInfoResponse.class);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(
			lettuceConnectionFactory);
		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
			.entryTtl(Duration.ofSeconds(3600)); // caching until 1 hour
		builder.cacheDefaults(cacheConfiguration);
		return builder.build();
	}
}
