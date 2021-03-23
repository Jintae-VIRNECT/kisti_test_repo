package com.virnect.message.global.config.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.virnect.message.dao.RetryMessageRepository;
import com.virnect.message.domain.MessageType;
import com.virnect.message.domain.RetryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Project: PF-Message
 * DATE: 2021-03-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public class CustomReturnCallback implements RabbitTemplate.ReturnCallback {
    private final RetryMessageRepository retryMessageRepository;
    private static final int MAX_RETRY_COUNT = 5;

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println(replyCode);
        if (replyCode == AMQP.NO_ROUTE && routingKey.startsWith(MessageType.EVENT.getValue())) {
            String messageBody = "";
            try {
                messageBody = new String(message.getBody(), Charset.defaultCharset().name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RetryMessage retryMessage = RetryMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .messageType(MessageType.EVENT)
                    .exchange(exchange)
                    .routingKey(routingKey)
                    .message(messageBody)
                    .errorCode(replyCode).build();
            retryMessageRepository.save(retryMessage);
        }
    }
}
