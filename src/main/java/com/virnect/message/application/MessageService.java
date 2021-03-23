package com.virnect.message.application;

import java.io.IOException;

import javax.mail.MessagingException;

import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.EmailSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.global.common.ApiResponse;

/**
 * Project: PF-Message
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface MessageService {
	ApiResponse<Boolean> sendAttachmentMail(AttachmentMailRequest mailSendRequest) throws
		MessagingException,
		IOException;

	ApiResponse<Boolean> sendMail(MailSendRequest mailSendRequest);

}
