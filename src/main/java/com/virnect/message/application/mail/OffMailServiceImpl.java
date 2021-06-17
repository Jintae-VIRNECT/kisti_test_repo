package com.virnect.message.application.mail;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.MailSendRequest;

/**
 * Project: PF-Message
 * DATE: 2021-05-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Profile("onpremise")
public class OffMailServiceImpl implements MailService {
	@Override
	public void sendTemplateMail(
		String sender, List<String> receivers, String subject, String mailTemplate, Context context
	) {

	}

	@Override
	public Boolean sendAttachmentMail(AttachmentMailRequest mailSendRequest) throws MessagingException, IOException {
		return null;
	}

	@Override
	public Boolean sendMail(MailSendRequest mailSendRequest) {
		return null;
	}
}
