package com.virnect.workspace.global.common;

import com.virnect.workspace.infra.mail.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-31
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile("!production")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)

public class JavaMailService implements MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public void sendTemplateMail(String sender, String to, String subject, String template, Context context) {
        String html = springTemplateEngine.process(template, context);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setSubject(subject);
            message.setText(html, "UTF-8", "html");
            message.setFrom(sender);
            message.addRecipients(Message.RecipientType.TO, to);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
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

