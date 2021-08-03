package com.virnect.uaa.global.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.websocket.WebSocketHandler;

@Configuration
@EnableRedisRepositories
@Slf4j
public class RedisConfiguration {
	private static final String FORCE_LOGOUT_CHANNEL = "force-logout";
	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		log.info("REDIS HOST: {}", this.host);
		log.info("REDIS PORT: {}", this.port);
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplateForObject(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
		return redisTemplate;
	}

	@Bean
	public MessageListenerAdapter messageListenerAdapter(WebSocketHandler webSocketHandler) {
		return new MessageListenerAdapter(webSocketHandler);
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		MessageListenerAdapter messageListenerAdapter, RedisConnectionFactory redisConnectionFactory
	) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
		redisMessageListenerContainer.addMessageListener(
			messageListenerAdapter, new ChannelTopic(FORCE_LOGOUT_CHANNEL));
		return redisMessageListenerContainer;
	}
}
