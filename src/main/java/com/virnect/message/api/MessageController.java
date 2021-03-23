package com.virnect.message.api;

import com.virnect.message.application.MessageService;
import com.virnect.message.domain.MessageType;
import com.virnect.message.dto.request.*;
import com.virnect.message.dto.response.EventSendResponse;
import com.virnect.message.dto.response.PushResponse;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
import com.virnect.message.global.config.rabbitmq.RabbitmqConfiguration;
import com.virnect.message.global.error.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Sample Basic Controller
 */
@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "MESSAGE API", consumes = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class MessageController {
    private final MessageService messageService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitmqConfiguration rabbitmqConfiguration;
    public static final String TOPIC_EXCHANGE_NAME = "amq.topic";


    @ApiOperation(
            value = "메일 메세지 발행",
            notes = "전송 타입 : Topics \n exchange name : email \n routing key : email.서비스명 (예시 routing key : email.pf-workspace)"
    )
    @PostMapping("/email")
    public void sendEMail(@RequestBody @Valid EmailSendRequest emailSendRequest, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (rabbitmqConfiguration.active) {
            String exchange = MessageType.EMAIL.getValue();
            String routingKey = exchange + "." + emailSendRequest.getService();
            rabbitTemplate.convertAndSend(exchange, routingKey, emailSendRequest);
            return;
        }
        this.messageService.sendEmailMessage(emailSendRequest);

    }

    @ApiOperation(
            value = "워크스페이스 푸시 메세지 발행",
            notes = "전송 타입 : Topics \n exchange name : topic \n routing key : push.서비스명.etc (예시 routing key : push.pf-workspace.4d6eab0860969a50acbfa4599fbb5ae8)"
    )
    @PostMapping("/push")
    public ResponseEntity<ApiResponse<PushResponse>> sendPush(@RequestBody @Valid PushSendRequest pushSendRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        String routingKey = String.format("%s.%s.%s", MessageType.PUSH.getValue(), pushSendRequest.getService(), pushSendRequest.getWorkspaceId());
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, routingKey, pushSendRequest);
        PushResponse pushResponse = new PushResponse(pushSendRequest.getService(), pushSendRequest.getEvent(), pushSendRequest.getWorkspaceId(), true, LocalDateTime.now());
        return ResponseEntity.ok(new ApiResponse<>(pushResponse));
    }

    @ApiOperation(
            value = "메일 전송",
            notes = "메일 전송에 사용하던 api",
            tags = "old message-controller"
    )
    @PostMapping("/mail")
    public ResponseEntity<ApiResponse<Boolean>> sendMail(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = messageService.sendMail(mailSendRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "첨부파일 메일 전송"
    )
    @PostMapping(value = "/mail/attachment")
    public ResponseEntity<ApiResponse<Boolean>> sendAttachmentMail(@RequestBody @Valid AttachmentMailRequest mailSendRequest, BindingResult bindingResult) throws MessagingException, IOException {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = messageService.sendAttachmentMail(mailSendRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "event 메세지 발행",
            notes = "전송 타입 : Topics \n exchange name : topic \n routing key : event.{eventName}.{eventUUID} (예시 routing key : event.session_flush.4d6eab0860969a50acbfa4599fbb5ae8)"
    )
    @PostMapping("/event")
    public ResponseEntity<ApiResponse<EventSendResponse>> sendEvent(@RequestBody @Valid EventSendRequest eventSendRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        String routingKey = String.format("%s.%s.%s", MessageType.EVENT.getValue(), eventSendRequest.getEventName(), eventSendRequest.getEventUUID());
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, routingKey, eventSendRequest);
        EventSendResponse eventSendResponse = new EventSendResponse(eventSendRequest.getService(), eventSendRequest.getEventName(), eventSendRequest.getEventUUID(), true, LocalDateTime.now());
        return ResponseEntity.ok(new ApiResponse<>(eventSendResponse));
    }

}
