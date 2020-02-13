package com.virnect.message.application;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.*;
import com.virnect.message.domain.MailTemplate;
import com.virnect.message.global.common.ResponseMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
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
@Profile("production")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AWSMailServiceImpl implements MailService {
    private final AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient;
    private final SpringTemplateEngine springTemplateEngine;


    public ResponseMessage sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context) {
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
        return new ResponseMessage();
    }

    private Content createContent(String data) {
        return new Content().withCharset("UTF-8").withData(data);
    }

    @Override
    public void sendStringMail(String sender, String to, String subject, String context) {

    }


}
