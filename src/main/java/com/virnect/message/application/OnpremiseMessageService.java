package com.virnect.message.application;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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
@Service
@Profile("onpremise")
public class OnpremiseMessageService implements MessageService{
	@Override
	public ApiResponse<Boolean> sendAttachmentMail(
		AttachmentMailRequest mailSendRequest
	) {
		return null;
	}

	@Override
	public ApiResponse<Boolean> sendMail(
		MailSendRequest mailSendRequest
	) {
		return null;
	}

}
