package com.virnect.message.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;

import java.nio.charset.StandardCharsets;

/**
 * Project: PF-Message
 * DATE: 2020-06-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
public class RabbitmqExceptionHandler extends RejectAndDontRequeueRecoverer {
    @Override
    public void recover(Message message, Throwable cause) {
        final byte[] body = message.getBody();
        final String msg = new String(body, StandardCharsets.UTF_8);
        log.error("===================");
        log.debug(msg);
        log.warn("Retries exhausted for message " + message, cause);
        log.error("===================");
    }
}
