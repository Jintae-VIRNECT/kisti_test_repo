package com.virnect.content.infra.message;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestmqService implements MessageService {
	@Override
	public void convertAndSend(String exchangeName, String routingKey, Object pushRequest) {
	}
}
