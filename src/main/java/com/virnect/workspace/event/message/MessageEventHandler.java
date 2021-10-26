package com.virnect.workspace.event.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.dto.rest.MailRequest;
import com.virnect.workspace.dto.rest.PushSendRequest;
import com.virnect.workspace.global.constant.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * Project: PF-Workspace
 * DATE: 2021-07-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class MessageEventHandler {
    private final MessageSource messageSource;
    private final SpringTemplateEngine springTemplateEngine;
    private final MessageRestService messageRestService;

    @EventListener(MailSendEvent.class)
    public void sendMailEventListener(MailSendEvent mailSendEvent) {
        log.info("[SEND MAIL EVENT] - [{}]", mailSendEvent.toString());
        Context context = mailSendEvent.getContext();
        String subject = messageSource.getMessage(mailSendEvent.getMailType().getSubject(), null, mailSendEvent.getLocale());
        String template = messageSource.getMessage(mailSendEvent.getMailType().getTemplate(), null, mailSendEvent.getLocale());
        String html = springTemplateEngine.process(template, context);
        MailRequest mailRequest = new MailRequest();
        mailRequest.setHtml(html);
        mailRequest.setReceivers(mailSendEvent.getReceiverList());
        mailRequest.setSender(MailSender.MASTER.getValue());
        mailRequest.setSubject(subject);
        messageRestService.sendMail(mailRequest);
    }

    @EventListener(GuestUserDeletedEvent.class)
    public void inviteSessionDeleteEventListener(GuestUserDeletedEvent guestUserDeletedEvent) {
        log.info("[GUEST USER DELETED EVENT] - [{}]", guestUserDeletedEvent.toString());
        Map<Object, Object> pushMessage = new HashMap<>();
        pushMessage.put("productNames", guestUserDeletedEvent.getGuestUserProductList());

        List<String> targetUserList = new ArrayList<>();
        targetUserList.add(guestUserDeletedEvent.getReceiveUserId());

        PushSendRequest pushSendRequest = new PushSendRequest();
        pushSendRequest.setService("remote");
        pushSendRequest.setEvent("guestUserDeleted");
        pushSendRequest.setWorkspaceId(guestUserDeletedEvent.getReceiveWorkspaceId());
        pushSendRequest.setTargetUserIds(targetUserList);
        pushSendRequest.setUserId(guestUserDeletedEvent.getSendUserId());
        pushSendRequest.setContents(pushMessage);

        messageRestService.sendPush(pushSendRequest);
    }
}
