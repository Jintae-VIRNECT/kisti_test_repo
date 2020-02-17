package com.virnect.message.application;

import com.virnect.message.domain.MailTemplate;
import com.virnect.message.dto.ContactRequestDTO;
import com.virnect.message.global.common.ResponseMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;

import java.util.List;

public interface MailService {
    ResponseMessage sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context);
    void sendStringMail(String sender, String to, String subject, String context); //test용
}
