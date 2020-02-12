package com.virnect.message.domain.application;

import com.virnect.message.domain.dto.MailRequestDTO;
import com.virnect.message.global.common.ResponseMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile("!production")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaMailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public ResponseMessage sendTemplateMail(MailRequestDTO mailRequestDTO) {
        Context context = new Context();
        context.setVariables(mailRequestDTO.getContext());
        String html = springTemplateEngine.process(mailRequestDTO.getTemplate(), context);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setSubject(mailRequestDTO.getSubject());
            message.setText(html, "UTF-8", "html");
            message.setFrom(mailRequestDTO.getSender());
            for(String receiver : mailRequestDTO.getReceiver()){
                message.addRecipients(Message.RecipientType.TO, receiver);
                javaMailSender.send(message);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new ResponseMessage();
    }

    @Override
    public void sendStringMail(String sender, String to, String subject, String context) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setSubject(subject);
            message.setText(context);
            message.setFrom(sender);
            message.addRecipients(Message.RecipientType.TO, to);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}

