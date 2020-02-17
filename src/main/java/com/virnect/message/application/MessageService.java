package com.virnect.message.application;

import com.virnect.message.domain.MailSender;
import com.virnect.message.domain.MailSubject;
import com.virnect.message.domain.MailTemplate;
import com.virnect.message.dto.ContactRequestDTO;
import com.virnect.message.dto.InviteWorkspaceRequestDTO;
import com.virnect.message.global.common.ResponseMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;

import java.net.ConnectException;
import java.util.*;

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

    /**
     * Contact 메일 전송
     *
     * @param contactRequestDTO - 메일 요청 정보
     * @return
     */
    public ResponseMessage sendContactMail(ContactRequestDTO contactRequestDTO) {
        String categories = Arrays.toString(contactRequestDTO.getCategory());
        String contactResponseSubject = MailSubject.CONTACT.getSubject();

        Context context = new Context();
        context.setVariable("name", contactRequestDTO.getSenderName());
        context.setVariable("category", categories);
        context.setVariable("email", contactRequestDTO.getSenderEmail());
        context.setVariable("content", contactRequestDTO.getContent());

        String contactRequestSubject = "[Platform] " + categories + " 관련 고객 문의";

        //1. 관리자에게 메일 전송
        this.mailService.sendTemplateMail(MailSender.MASTER.getSender(), Collections.singletonList(MailSender.CONTACT.getSender()), contactRequestSubject
                , MailTemplate.CONTACT_REQUEST.getTemplate(), context);
        log.info("[문의 메일 관리자 전송 완료] - [발신자 이메일 - " + Collections.singletonList(MailSender.CONTACT.getSender()) + "]" + " - [문의 종류] - " + categories);


        //2. 발신자에게 접수 완료 메일 전송
        mailService.sendTemplateMail(MailSender.MASTER.getSender(), Collections.singletonList(contactRequestDTO.getSenderEmail()), contactResponseSubject, MailTemplate.CONTACT_RESPONSE.getTemplate(), context);
        log.info("[문의 메일 접수 완료] - [발신자 이메일 - " + Collections.singletonList(contactRequestDTO.getSenderEmail()) + "]");

        return new ResponseMessage().addParam("sendContactMail", true);
    }

    /**
     * 워크스페이스 초대 메일 전송
     *
     * @param inviteWorkspaceRequestDTO - 워크스페이스 초대 정보
     * @return
     */
    public ResponseMessage sendInviteWorkspaceMail(InviteWorkspaceRequestDTO inviteWorkspaceRequestDTO) {
        //1. 초대 된 사람에게 메일 발송
        Context context = new Context();
        String inviteAcceptUrl = inviteWorkspaceRequestDTO.getAcceptUrl() + "?userId=" + inviteWorkspaceRequestDTO.getRequestUserId() + "&code=" + inviteWorkspaceRequestDTO.getInviteCode();
        context.setVariable("masterName", inviteWorkspaceRequestDTO.getRequestUserName());
        context.setVariable("inviteAcceptUrl", inviteAcceptUrl);
        context.setVariable("acceptUrl", inviteAcceptUrl);

        List<String> inviteUserEmail = new ArrayList<>();
        for (Map<String, String> inviteInfo : inviteWorkspaceRequestDTO.getInviteInfos()) {
            context.setVariable("inviteUserName", inviteInfo.get("inviteUserName"));
            inviteUserEmail.add(inviteInfo.get("inviteUserEmail"));
        }
        mailService.sendTemplateMail(MailSender.MASTER.getSender(), inviteUserEmail, MailSubject.CONTACT.getSubject(), MailTemplate.WORKSPACE_INVITE_ACCEPT.getTemplate(), context);
        log.info("[초대 메일 전송 완료] - [초대자 이메일 - " + inviteUserEmail.toArray() + "]");

        return new ResponseMessage().addParam("sendInviteAcceptMail", true);

    }
}

