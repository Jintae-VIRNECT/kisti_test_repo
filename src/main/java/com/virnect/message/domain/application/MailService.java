package com.virnect.message.domain.application;

import com.virnect.message.domain.dto.ContactRequestDTO;
import com.virnect.message.global.common.ResponseMessage;

public interface MailService {
    ResponseMessage sendTemplateMail(ContactRequestDTO mailRequestDTO);
    void sendStringMail(String sender, String to, String subject, String context); //test용
}
