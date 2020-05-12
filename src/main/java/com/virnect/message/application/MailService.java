package com.virnect.message.application;

import org.thymeleaf.context.Context;

import java.util.List;

public interface MailService {
    void sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context);

    void sendMail(String receivers, String sender, String subject, String html);

}
