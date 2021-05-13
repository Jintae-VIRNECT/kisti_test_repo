package com.virnect.message.application.mail;

import org.springframework.context.annotation.Profile;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.global.common.ApiResponse;

@Profile("!onpremise")
public interface MailService {
    void sendTemplateMail(String sender, List<String> receivers, String subject, String mailTemplate, Context context);

    Boolean sendAttachmentMail(AttachmentMailRequest mailSendRequest) throws MessagingException, IOException;

    Boolean sendMail(MailSendRequest mailSendRequest);
}
