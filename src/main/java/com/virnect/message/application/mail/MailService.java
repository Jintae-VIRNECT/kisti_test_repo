package com.virnect.message.application.mail;

import org.springframework.context.annotation.Profile;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
@Profile("!onpremise")
public interface MailService {
    void sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context);

    void sendMail(String receivers, String sender, String subject, String html);

    void sendAttachmentMail(String receivers, String sender, String subject, String html, byte[] multipartFile, String fileName) throws MessagingException, IOException;
}
