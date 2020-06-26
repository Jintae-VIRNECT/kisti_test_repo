package com.virnect.message.global.config;

import com.virnect.message.global.error.RabbitmqExceptionHandler;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project: PF-Message
 * DATE: 2020-06-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Configuration
public class RabbitmqConfiguration {
    private final int MAX_TRY_COUNT = 3;
    private final int INITIAL_INTERVAL = 3000;
    private final int MULTIPLIER = 3;
    private final int MAX_INTERVAL = 10000;

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    private Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);//예외가 발생하면 다시 큐에 쌓임
        factory.setMessageConverter(messageConverter());
        /*factory.setAdviceChain(RetryInterceptorBuilder
                .stateless()
                .maxAttempts(MAX_TRY_COUNT)
                .recoverer(new RabbitmqExceptionHandler())
                .backOffOptions(INITIAL_INTERVAL, MULTIPLIER, MAX_INTERVAL)
                .build());*/
        return factory;
    }

}
