package com.virnect.content.api;

import static com.github.fridujo.rabbitmq.mock.exchange.MockExchangeCreator.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import com.github.fridujo.rabbitmq.mock.exchange.MockTopicExchange;

@TestConfiguration
public class EmbeddedRabbitmqConfiguration {
	@Bean
	ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory(
			MockConnectionFactoryFactory
				.build()
				.withAdditionalExchange(creatorWithExchangeType("amq.topic", MockTopicExchange::new))
				.enableConsistentHashPlugin()
		);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		TopicExchange exchange = new TopicExchange("amq.topic");
		rabbitAdmin.declareExchange(exchange);
		Queue apiQueue = new Queue("api-sub-queue");
		Queue pushQueue = new Queue("push-sub-queue");
		rabbitAdmin.declareQueue(apiQueue);
		rabbitAdmin.declareQueue(pushQueue);
		rabbitAdmin.declareBinding(BindingBuilder.bind(apiQueue).to(exchange).with("api.*"));
		rabbitAdmin.declareBinding(BindingBuilder.bind(pushQueue).to(exchange).with("push.*"));
		return rabbitAdmin;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}

	@Bean
	SimpleMessageListenerContainer container(
		ConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter
	) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter() {
		return new MessageListenerAdapter(new Receiver(), "receiveMessage");
	}

	static class Receiver {
		private final List<String> messages = new ArrayList<>();

		public void receiveMessage(String message) {
			this.messages.add(message);
		}

		public String receiveMessageAndReply(String message) {
			this.messages.add(message);
			return new StringBuilder(message).reverse().toString();
		}

		List<String> getMessages() {
			return messages;
		}
	}
}