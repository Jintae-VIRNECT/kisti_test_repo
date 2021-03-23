package com.virnect.message.application.mail;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;


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
@Profile("!onpremise")
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

    @Override
    public void sendAttachmentMail(String receiver, String sender, String subject, String html, byte[] bytes, String fileName) throws MessagingException, IOException {

        Session session = Session.getDefaultInstance(new Properties());

        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(receiver));

        MimeMultipart msg_body = new MimeMultipart("alternative");
        MimeBodyPart wrap = new MimeBodyPart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        msg_body.addBodyPart(htmlPart);
        wrap.setContent(msg_body);

        MimeMultipart msg = new MimeMultipart("mixed");
        message.setContent(msg);
        msg.addBodyPart(wrap);

        //첨부파일
        File convertFile = new File("버넥트 솔루션 도입 ROI 측정 결과.pdf");
        if (convertFile.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(convertFile);
            fos.write(bytes);
            fos.close();
        }
        DataSource dataSource = new ByteArrayDataSource(bytes, "application/octet-stream", "버넥트 솔루션 도입 ROI 측정 결과.pdf");
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDataHandler(new DataHandler(dataSource));
        bodyPart.setFileName("버넥트 솔루션 도입 ROI 측정 결과.pdf");
        msg.addBodyPart(bodyPart);

        try {
            PrintStream out = System.out;
            message.writeTo(out);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage =
                    new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest rawEmailRequest =
                    new SendRawEmailRequest(rawMessage);

            amazonSimpleEmailServiceAsyncClient.sendRawEmail(rawEmailRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        convertFile.delete();
    }


}
