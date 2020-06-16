package com.virnect.message.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.message.dao.MailHistoryRepository;
import com.virnect.message.domain.MailHistory;
import com.virnect.message.domain.MailSender;
import com.virnect.message.dto.request.EmailSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.dto.request.PushSendRequest;
import com.virnect.message.global.common.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Project: PF-Message
 * DATE: 2020-02-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageService {
    private final MailService mailService;
    private final MailHistoryRepository mailHistoryRepository;
    private final ObjectMapper objectMapper;

    public ApiResponse<Boolean> sendMail(MailSendRequest mailSendRequest) {

        for (String receiver : mailSendRequest.getReceivers()) {
            MailHistory mailHistory = MailHistory.builder()
                    .receiver(receiver)
                    .sender(MailSender.MASTER.getSender())
                    .contents(mailSendRequest.getHtml())
                    .subject(mailSendRequest.getSubject())
                    .resultCode(HttpStatus.OK.value())
                    .build();
            mailService.sendMail(receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml());

            log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

            this.mailHistoryRepository.save(mailHistory);
        }

        return new ApiResponse<>(true);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "email", type = ExchangeTypes.TOPIC),
            key = "email.*"
    ))
    public void sendEmailMessage(EmailSendRequest emailSendRequest) throws IOException {
        for (String receiver : emailSendRequest.getReceivers()) {
            MailHistory mailHistory = MailHistory.builder()
                    .receiver(receiver)
                    .sender(MailSender.MASTER.getSender())
                    .contents(emailSendRequest.getHtml())
                    .subject(emailSendRequest.getSubject())
                    .resultCode(HttpStatus.OK.value())
                    .build();
            mailService.sendMail(receiver, MailSender.MASTER.getSender(), emailSendRequest.getSubject(), emailSendRequest.getHtml());

            log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

            this.mailHistoryRepository.save(mailHistory);
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "push", type = ExchangeTypes.TOPIC),
            key = "push.#"
    ))
    public void getAllPushMessage(PushSendRequest pushSendRequest) throws IOException {
        log.info(pushSendRequest.toString());
    }


}

