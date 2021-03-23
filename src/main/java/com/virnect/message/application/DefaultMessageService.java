package com.virnect.message.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.message.dao.MailHistoryRepository;
import com.virnect.message.domain.MailHistory;
import com.virnect.message.domain.MailSender;
import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.EmailSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.dto.request.PushSendRequest;
import com.virnect.message.global.common.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
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
@Profile("!onpremise")
public class DefaultMessageService implements MessageService {
    private final MailService mailService;
    private final MailHistoryRepository mailHistoryRepository;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final AmazonS3 amazonS3Client;

    public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";
    public static final int MAX_RETRY_COUNT = 2;
    public static final String S3_BUCKET_NAME = "virnect-homepagestorage";

    public ApiResponse<Boolean> sendMail(MailSendRequest mailSendRequest) {

        for (String receiver : mailSendRequest.getReceivers()) {
            MailHistory mailHistory = MailHistory.builder()
                    .receiver(receiver)
                    .sender(MailSender.MASTER.getSender())
                    .contents(mailSendRequest.getHtml())
                    .subject(mailSendRequest.getSubject())
                    .resultCode(HttpStatus.OK.value())
                    .build();
            mailService.sendMail(
                    receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml());

            log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

            this.mailHistoryRepository.save(mailHistory);
        }

        return new ApiResponse<>(true);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "message.email", type = ExchangeTypes.TOPIC),
            key = "email.*"
    ), containerFactory = "rabbitListenerContainerFactory")
    public void sendEmailMessage(EmailSendRequest emailSendRequest) {
        for (String receiver : emailSendRequest.getReceivers()) {
            MailHistory mailHistory = MailHistory.builder()
                    .receiver(receiver)
                    .sender(MailSender.MASTER.getSender())
                    .contents(emailSendRequest.getHtml())
                    .subject(emailSendRequest.getSubject())
                    .resultCode(HttpStatus.OK.value())
                    .build();
            mailService.sendMail(
                    receiver, MailSender.MASTER.getSender(), emailSendRequest.getSubject(), emailSendRequest.getHtml());

            log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

            this.mailHistoryRepository.save(mailHistory);
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(arguments = {@Argument(name = "x-dead-letter-exchange", value = "dlx"),
                    @Argument(name = "x-dead-letter-routing-key", value = "dlx.push")}),
            exchange = @Exchange(value = "amq.topic", type = ExchangeTypes.TOPIC),
            key = "push.#"
    ), containerFactory = "rabbitListenerContainerFactory")
    public void getAllPushMessage(PushSendRequest pushSendRequest) throws IOException {
        log.info(pushSendRequest.toString());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "dlx", type = ExchangeTypes.TOPIC),
            key = "dlx.*"
    ), containerFactory = "rabbitListenerContainerFactory")
    public void getWaitMessage(Message failedMessage) throws IOException {
        Integer retriesCnt = (Integer) failedMessage.getMessageProperties().getHeaders().get(HEADER_X_RETRIES_COUNT);

        if (retriesCnt == null)
            retriesCnt = 1;

        if (retriesCnt > MAX_RETRY_COUNT) {
            log.info("Sending message to the parking lot queue");
            rabbitTemplate.send(
                    "plx", "plx." + failedMessage.getMessageProperties().getReceivedExchange(), failedMessage);
            return;
        }
        log.info("Retrying message for the {} time", retriesCnt);
        failedMessage.getMessageProperties().getHeaders().put(HEADER_X_RETRIES_COUNT, ++retriesCnt);

        rabbitTemplate.convertAndSend(
                failedMessage.getMessageProperties().getReceivedExchange(),
                failedMessage.getMessageProperties().getReceivedRoutingKey(), failedMessage
        );
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "plx", type = ExchangeTypes.TOPIC),
            key = "plx.*"
    ), containerFactory = "rabbitListenerContainerFactory")
    public void getDeadMessage(Message deadMessage) throws IOException {
        log.info(deadMessage.toString());
    }

    public ApiResponse<Boolean> sendAttachmentMail(AttachmentMailRequest mailSendRequest) throws
            MessagingException,
            IOException {
        S3Object object = amazonS3Client.getObject(
                S3_BUCKET_NAME, "roi/" + FilenameUtils.getName(mailSendRequest.getMultipartFile()));
        S3ObjectInputStream inputStream = object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(inputStream, object.getObjectMetadata().getContentLength());

        for (String receiver : mailSendRequest.getReceivers()) {
            MailHistory mailHistory = MailHistory.builder()
                    .receiver(receiver)
                    .sender(MailSender.MASTER.getSender())
                    .contents(mailSendRequest.getHtml())
                    .subject(mailSendRequest.getSubject())
                    .resultCode(HttpStatus.OK.value())
                    .build();

            mailService.sendAttachmentMail(
                    receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml(), bytes,
                    FilenameUtils.getName(mailSendRequest.getMultipartFile())
            );

            log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

            this.mailHistoryRepository.save(mailHistory);
        }

        return new ApiResponse<>(true);
    }
    /*@RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "amq.topic", type = ExchangeTypes.TOPIC),
            key = "event.#"
    ), containerFactory = "rabbitListenerContainerFactory")
    public void getEventMessage(Message message) {
        log.info(message.toString());
    }*/
}

