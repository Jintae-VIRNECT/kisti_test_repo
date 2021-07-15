package com.virnect.message.api;

import com.virnect.message.application.message.MessageService;
import com.virnect.message.domain.MessageType;
import com.virnect.message.dto.request.*;
import com.virnect.message.dto.response.EventSendResponse;
import com.virnect.message.dto.response.PushResponse;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
import com.virnect.message.global.error.ErrorCode;

import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
public class MessageController {
	private final MessageService messageService;

	@ApiOperation(
		value = "워크스페이스 푸시 메세지 발행",
		notes = "전송 타입 : Topics \n exchange name : topic \n routing key : push.서비스명.etc (예시 routing key : push.pf-workspace.4d6eab0860969a50acbfa4599fbb5ae8)"
	)
	@PostMapping("/push")
	public ResponseEntity<ApiResponse<PushResponse>> pushMessageHandler(
		@RequestBody @Valid PushSendRequest pushSendRequest, BindingResult bindingResult
	) {
		if (bindingResult.hasErrors()) {
			throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		PushResponse responseMessage = messageService.pushMessageHandler(pushSendRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(
		value = "event 메세지 발행",
		notes = "전송 타입 : Topics \n exchange name : topic \n routing key : event.{eventName}.{eventUUID} (예시 routing key : event.session_flush.4d6eab0860969a50acbfa4599fbb5ae8)"
	)
	@PostMapping("/event")
	public ResponseEntity<ApiResponse<EventSendResponse>> eventMessageHandler(
		@RequestBody @Valid EventSendRequest eventSendRequest, BindingResult bindingResult
	) {
		if (bindingResult.hasErrors()) {
			throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		EventSendResponse responseMessage = messageService.eventMessageHandler(eventSendRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
