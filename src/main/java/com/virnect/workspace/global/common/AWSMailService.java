package com.virnect.workspace.global.common;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.*;
import com.virnect.workspace.infra.mail.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AWSMailService implements MailService {
    private final AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient;
    private final SpringTemplateEngine springTemplateEngine;


    public void sendTemplateMail(String sender, String to, String subject, String template, Context context) {
        String html = this.springTemplateEngine.process(template, context);

        Message message = new Message()
                .withSubject(createContent(subject))
                .withBody(new Body().withHtml(createContent(html)));

        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withSource(sender)
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(message);
        this.amazonSimpleEmailServiceAsyncClient.sendEmailAsync(sendEmailRequest);
    }

    private Content createContent(String data) {
        return new Content().withCharset("UTF-8").withData(data);
    }

    @Override
    public void sendStringMail(String sender, String to, String subject, String context) {

    }
}
