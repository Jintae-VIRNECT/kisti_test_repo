package com.virnect.content.infra.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("!test")
@Component
public class RabbitmqService implements MessageService {
	private final RabbitTemplate rabbitTemplate;

	@Override
	public void convertAndSend(
		String exchangeName, String routingKey, Object pushRequest
	) {
		rabbitTemplate.convertAndSend(exchangeName, routingKey, pushRequest);
	}
}
