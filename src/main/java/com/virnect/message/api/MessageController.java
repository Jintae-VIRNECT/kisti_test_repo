package com.virnect.message.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.message.application.MessageService;
import com.virnect.message.domain.MessageType;
import com.virnect.message.dto.request.EmailSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.dto.request.PushSendRequest;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
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
import java.util.function.Supplier;

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
    private final ObjectMapper objectMapper;

    @ApiOperation(
            value = "메일 메세지 발행"
    )
    @PostMapping("/email")
    public void sendEMail(@RequestBody @Valid EmailSendRequest emailSendRequest, BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        String exchange = MessageType.EMAIL.getValue();
        String routingKey = exchange + "." + emailSendRequest.getService();

        rabbitTemplate.convertAndSend(exchange, routingKey, emailSendRequest);
    }

    @ApiOperation(
            value = "푸시 메세지 발행"
    )
    @PostMapping("/push")
    public void sendPush(@RequestBody @Valid PushSendRequest pushSendRequest, BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        String exchange = MessageType.PUSH.getValue();
        String routingKey = exchange + "." + pushSendRequest.getService() + "." + pushSendRequest.getWorkspaceId() + "." + pushSendRequest.getUserId();
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

}
