package com.virnect.message.domain.application;

import com.virnect.message.domain.dto.MailRequestDTO;
import com.virnect.message.global.common.ResponseMessage;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

public interface MailService {
    ResponseMessage sendTemplateMail(MailRequestDTO mailRequestDTO);
    void sendStringMail(String sender, String to, String subject, String context); //test용
}
