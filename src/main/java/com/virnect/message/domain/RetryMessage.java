package com.virnect.message.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

/**
 * Project: PF-Message
 * DATE: 2021-03-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
//@RedisHash(value = "RetryMessage", timeToLive = 10)
public class RetryMessage {
    @Id
    private String id;
    private MessageType messageType;
    private String exchange;
    private String routingKey;
    private String message;
    private Integer errorCode;

    @Builder
    public RetryMessage(String id, MessageType messageType, String exchange, String routingKey, String message, Integer errorCode) {
        this.id = id;
        this.messageType = messageType;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.message = message;
        this.errorCode = errorCode;
    }
}

