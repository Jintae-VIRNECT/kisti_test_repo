package com.virnect.message.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.message.application.MessageService;
import com.virnect.message.domain.MessageType;
import com.virnect.message.dto.request.EmailSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.dto.request.PushSendRequest;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
import com.virnect.message.global.config.RabbitmqConfiguration;
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

import javax.validation.Valid;
import java.io.IOException;

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
    private final ObjectMapper objectMapper;

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
            value = "푸시 메세지 발행",
            notes = "전송 타입 : Topics \n exchange name : topic \n routing key : push.서비스명.etc (예시 routing key : push.pf-workspace.4d6eab0860969a50acbfa4599fbb5ae8)"
    )
    @PostMapping("/push")
    public void sendPush(@RequestBody @Valid PushSendRequest pushSendRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        //String exchange = MessageType.PUSH.getValue();
        //String routingKey = exchange + "." + pushSendRequest.getService() + "." + pushSendRequest.getWorkspaceId();
        String exchange = "amq.topic";
        String routingKey = "push" + "." + pushSendRequest.getService() + "." + pushSendRequest.getWorkspaceId();
        rabbitTemplate.convertAndSend(exchange, routingKey, pushSendRequest);

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

    @GetMapping("/test")
    private void addMessage() {

        System.out.println("test message receive");
        rabbitTemplate.convertAndSend("amq.topic", "testqueue", "test message");
    }

}
