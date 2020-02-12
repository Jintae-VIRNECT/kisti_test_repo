package com.virnect.message.domain.application;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.*;
import com.virnect.message.domain.dto.MailRequestDTO;
import com.virnect.message.global.common.ResponseMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;


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


    public ResponseMessage sendTemplateMail(MailRequestDTO mailRequestDTO) {
        Context context = new Context();
        context.setVariables(mailRequestDTO.getContext());
        String html = this.springTemplateEngine.process(mailRequestDTO.getTemplate(), context);

        Message message = new Message()
                .withSubject(createContent(mailRequestDTO.getSubject()))
                .withBody(new Body().withHtml(createContent(html)));

        for(String receiver : mailRequestDTO.getReceiver()){
            SendEmailRequest sendEmailRequest = new SendEmailRequest()
                    .withSource(mailRequestDTO.getSender())
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
