package com.virnect.message.application;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AWSMailServiceImpl implements MailService {
    private final AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient;
    private final SpringTemplateEngine springTemplateEngine;


    public void sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context) {
        String html = this.springTemplateEngine.process(mailTemplate, context);
        Message message = new Message()
                .withSubject(createContent(subject))
                .withBody(new Body().withHtml(createContent(html)));

        for (String receiver : receivers) {
            SendEmailRequest sendEmailRequest = new SendEmailRequest()
                    .withSource(sender)
                    .withDestination(new Destination().withToAddresses(receiver))
                    .withMessage(message);
            this.amazonSimpleEmailServiceAsyncClient.sendEmailAsync(sendEmailRequest);
        }
    }

    private Content createContent(String data) {
        return new Content().withCharset("UTF-8").withData(data);
    }

    @Override
    public void sendMail(String receiver, String sender, String subject, String html) {
        Message message = new Message()
                .withSubject(createContent(subject))
                .withBody(new Body().withHtml(createContent(html)));

        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withSource(sender)
                .withDestination(new Destination().withToAddresses(receiver))
                .withMessage(message);
        this.amazonSimpleEmailServiceAsyncClient.sendEmailAsync(sendEmailRequest);

    }
}
