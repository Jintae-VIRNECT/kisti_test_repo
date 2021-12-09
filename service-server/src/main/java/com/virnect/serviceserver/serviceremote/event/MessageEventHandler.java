package com.virnect.serviceserver.serviceremote.event;

import java.util.Collections;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.serviceserver.serviceremote.application.ServiceSessionManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageEventHandler {

	private final ServiceSessionManager serviceSessionManager;
	private final MemberRepository memberRepository;

	@EventListener
	public void sendMessage(MessageEvent messageEvent) {
		switch (messageEvent.getEventRequest().getEvent()) {
			case DELETED_ACCOUNT:
				Member guestMember = memberRepository.findGuestMemberByWorkspaceIdAndUuid(
					messageEvent.getEventRequest().getWorkspaceId(),
					messageEvent.getEventRequest().getUserId()
				).orElse(null);
				if (!ObjectUtils.isEmpty(guestMember)) {
					JsonObject jsonObject = serviceSessionManager.generateMessage(
						guestMember.getSessionId(),
						Collections.singletonList(guestMember.getConnectionId()),
						messageEvent.getSignalType(),
						messageEvent.getEventRequest().getEvent().getMessage()
					);
					if (jsonObject.has("error")) {
						log.info("[Send signal result] : error({})", jsonObject.get("error").getAsString());
					} else {
						log.info(
							"[Evict participant result] : {}",
							serviceSessionManager.evictParticipant(
								guestMember.getSessionId(),
								guestMember.getConnectionId()
							)
						);
					}
				}
		}
	}
}
