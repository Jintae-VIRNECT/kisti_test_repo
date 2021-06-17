package com.virnect.data.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.data.redis.domain.ForceLogoutMessage;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
	private final RedisTemplate<String, Object> redisTemplateForObject;

	public void publish(ChannelTopic channelTopic, ForceLogoutMessage message){
		redisTemplateForObject.convertAndSend(channelTopic.getTopic(), message);
	}
}