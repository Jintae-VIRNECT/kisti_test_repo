package com.virnect.message.application.message;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.EmailSendRequest;
import com.virnect.message.dto.request.EventSendRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.dto.request.PushSendRequest;
import com.virnect.message.dto.response.EventSendResponse;
import com.virnect.message.dto.response.PushResponse;
import com.virnect.message.global.common.ApiResponse;

/**
 * Project: PF-Message
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface MessageService {
	PushResponse pushMessageHandler(PushSendRequest pushSendRequest);
	EventSendResponse eventMessageHandler(EventSendRequest eventSendRequest);
	void getAllPushMessage(PushSendRequest pushSendRequest);

}
