package com.virnect.message.application;

import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface MailService {
    void sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context);
    void sendMail(String receivers, String sender, String subject, String html);
    void sendAttachmentMail(String receivers, String sender, String subject, String html, MultipartFile multipartFile) throws MessagingException, IOException;
}
