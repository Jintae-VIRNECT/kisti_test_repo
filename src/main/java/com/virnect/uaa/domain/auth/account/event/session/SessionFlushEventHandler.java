package com.virnect.uaa.domain.auth.account.event.session;

import java.util.HashMap;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.rest.message.MessageRestService;
import com.virnect.uaa.infra.rest.message.message.EventSendRequest;
import com.virnect.uaa.infra.rest.message.message.EventSendResponse;

/**
 * Project: PF-Auth
 * DATE: 2021-03-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionFlushEventHandler {
	private final MessageRestService messageRestService;

	@EventListener(SessionFlushEvent.class)
	public void sessionFlushEventListener(SessionFlushEvent sessionFlushEvent) {
		SessionInformation sessionInformation = sessionFlushEvent.getSession();
		log.info("Session Flush Event Info - {}", sessionInformation.toString());

		log.info("Session Flush Event Receive User - {}", sessionFlushEvent.getUserDetails().toString());

		EventSendResponse eventSendResponse = messageRestService.sendMessage(
			new EventSendRequest("session_flush", sessionFlushEvent.getUserDetails().getUuid(), "pf-auth", new HashMap<>())).getData();
		log.info("Session Flush Event Response - {}", eventSendResponse.toString());
	}

}
