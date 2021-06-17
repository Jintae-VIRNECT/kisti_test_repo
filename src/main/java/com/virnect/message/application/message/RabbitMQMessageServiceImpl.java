package com.virnect.message.application.message;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.message.application.mail.MailService;
import com.virnect.message.dao.MailHistoryRepository;
import com.virnect.message.domain.MailHistory;
import com.virnect.message.domain.MailSender;
import com.virnect.message.domain.MessageType;
import com.virnect.message.dto.request.EventSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.dto.request.PushSendRequest;
import com.virnect.message.dto.response.EventSendResponse;
import com.virnect.message.dto.response.PushResponse;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.error.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

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
public class RabbitMQMessageServiceImpl implements MessageService {
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	private final MailService mailService;

	public static final String TOPIC_EXCHANGE_NAME = "amq.topic";
	public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";
	public static final int MAX_RETRY_COUNT = 2;

	@Override
	public PushResponse pushMessageHandler(
		PushSendRequest pushSendRequest
	) {
		String routingKey = String.format(
			"%s.%s.%s", MessageType.PUSH.getValue(), pushSendRequest.getService(), pushSendRequest.getWorkspaceId());
		rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, routingKey, pushSendRequest);
		log.info(
			"[PUSH MESSAGE PUBLISH] exchange : [{}], routingKey : [{}], message : [{}]", TOPIC_EXCHANGE_NAME,
			routingKey, pushSendRequest.toString()
		);
		return new PushResponse(
			pushSendRequest.getService(), pushSendRequest.getEvent(), pushSendRequest.getWorkspaceId(), true,
			LocalDateTime
				.now()
		);
	}

	@Override
	public EventSendResponse eventMessageHandler(
		EventSendRequest eventSendRequest
	) {
		if (eventSendRequest.getMessageType() != null && eventSendRequest.getMessageType() == MessageType.EMAIL) {
			try {
				MailSendRequest mailSendRequest = objectMapper.readValue(
					eventSendRequest.getContents().get("mailSendRequest").toString(), MailSendRequest.class);
				mailService.sendMail(mailSendRequest);
			} catch (JsonProcessingException e) {
				throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
			}

			return new EventSendResponse(
				eventSendRequest.getService(), eventSendRequest.getEventName(), eventSendRequest.getEventUUID(), true,
				LocalDateTime.now()
			);
		}
		String routingKey = String.format(
			"%s.%s.%s", MessageType.EVENT.getValue(), eventSendRequest.getEventName(), eventSendRequest.getEventUUID());
		rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, routingKey, eventSendRequest);
		log.info(
			"[EVENT MESSAGE PUBLISH] exchange : [{}], routingKey : [{}], message : [{}]", TOPIC_EXCHANGE_NAME,
			routingKey, eventSendRequest.toString()
		);
		return new EventSendResponse(
			eventSendRequest.getService(), eventSendRequest.getEventName(), eventSendRequest.getEventUUID(), true,
			LocalDateTime.now()
		);
	}

	@Override
	@RabbitListener(bindings = @QueueBinding(
		value = @Queue(arguments = {@Argument(name = "x-dead-letter-exchange", value = "dlx"),
			@Argument(name = "x-dead-letter-routing-key", value = "dlx.push")}),
		exchange = @Exchange(value = "amq.topic", type = ExchangeTypes.TOPIC),
		key = "push.#"
	), containerFactory = "rabbitListenerContainerFactory")
	public void getAllPushMessage(PushSendRequest pushSendRequest) {
		log.info("[PUSH MESSAGE RECEIVED] MESSAGE >> [{}]", pushSendRequest.toString());
	}

	//이벤트 메세지는 라우팅할 큐가 있는지 체크하기 위해서 로깅하지 않음.
	/*@RabbitListener(bindings = @QueueBinding(
		value = @Queue,
		exchange = @Exchange(value = "amq.topic", type = ExchangeTypes.TOPIC),
		key = "event.#"
	), containerFactory = "rabbitListenerContainerFactory")
	public void getAllEventMessage(EventSendRequest eventSendRequest) {
		log.info("[EVENT MESSAGE RECEIVED] MESSAGE >> [{}]", eventSendRequest.toString());
	}*/

	@RabbitListener(bindings = @QueueBinding(
		value = @Queue,
		exchange = @Exchange(value = "dlx", type = ExchangeTypes.TOPIC),
		key = "dlx.*"
	), containerFactory = "rabbitListenerContainerFactory")
	public void getWaitMessage(Message failedMessage) {
		Integer retriesCnt = (Integer)failedMessage.getMessageProperties().getHeaders().get(HEADER_X_RETRIES_COUNT);

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
	public void getDeadMessage(Message deadMessage) {
		log.info(deadMessage.toString());
	}

}

