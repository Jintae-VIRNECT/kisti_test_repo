package com.virnect.message.application;

import com.virnect.message.global.common.ResponseMessage;
import org.thymeleaf.context.Context;

import java.util.List;

public interface MailService {
    ResponseMessage sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context);
    void sendStringMail(String sender, String to, String subject, String context); //test용
}
