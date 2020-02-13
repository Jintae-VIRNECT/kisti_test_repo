package com.virnect.message.application;

import com.virnect.message.global.common.ResponseMessage;
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
import java.util.List;


/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile({"local", "develop"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaMailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public ResponseMessage sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context) {
        String html = springTemplateEngine.process(mailTemplate, context);
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setSubject(subject);
            message.setText(html, "UTF-8", "html");
            message.setFrom(sender);
            for (String receiver : receivers) {
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

