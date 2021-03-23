package com.virnect.message.global.config.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.message.dao.RetryMessageRepository;
import com.virnect.message.domain.MessageType;
import com.virnect.message.domain.RetryMessage;
import com.virnect.message.dto.request.EventSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Project: PF-Message
 * DATE: 2021-03-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RetryMessageScheduler {
    private final RetryMessageRepository retryMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    //@Scheduled(fixedDelayString = "${schedule-interval}")
    public void eventMessageRetryHandler() throws JsonProcessingException {
        for (RetryMessage retryMessage : retryMessageRepository.findAll()) {
            if (retryMessage.getMessageType().equals(MessageType.EVENT)) {
                EventSendRequest eventSendRequest = objectMapper.readValue(retryMessage.getMessage(), EventSendRequest.class);
                rabbitTemplate.convertAndSend(retryMessage.getExchange(), retryMessage.getRoutingKey(), eventSendRequest);
                log.info("Retry Message Publish to Rabbitmq. message : {}", retryMessage.toString());
                retryMessageRepository.delete(retryMessage);
            }
        }
    }

}
