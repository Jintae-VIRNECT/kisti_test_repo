package com.virnect.message.application.mail;

import java.io.IOException;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.message.dao.MailHistoryRepository;
import com.virnect.message.domain.MailHistory;
import com.virnect.message.domain.MailSender;
import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.global.common.ByteArrayDataSource;
import com.virnect.message.infra.file.FileService;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Profile("!onpremise")
public class OnMailServiceImpl implements MailService {
	private final SpringTemplateEngine springTemplateEngine;
	private final MailHistoryRepository mailHistoryRepository;
	private final FileService fileService;
	private final JavaMailSender javaMailSender;

	@Override
	public Boolean sendMail(MailSendRequest mailSendRequest) {
		for (String receiver : mailSendRequest.getReceivers()) {
			MailHistory mailHistory = MailHistory.builder()
				.receiver(receiver)
				.sender(MailSender.MASTER.getSender())
				.contents(mailSendRequest.getHtml())
				.subject(mailSendRequest.getSubject())
				.resultCode(HttpStatus.OK.value())
				.build();
			this.send(receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml());

			log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

			this.mailHistoryRepository.save(mailHistory);
		}

		return true;
	}

	@Override
	public Boolean sendAttachmentMail(AttachmentMailRequest mailSendRequest) throws
		MessagingException,
		IOException {
		byte[] bytes = fileService.getObjectBytes("roi/" + FilenameUtils.getName(mailSendRequest.getMultipartFile()));

		for (String receiver : mailSendRequest.getReceivers()) {
			MailHistory mailHistory = MailHistory.builder()
				.receiver(receiver)
				.sender(MailSender.MASTER.getSender())
				.contents(mailSendRequest.getHtml())
				.subject(mailSendRequest.getSubject())
				.resultCode(HttpStatus.OK.value())
				.build();

			this.sendAttachment(
				receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml(),
				bytes
			);

			log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

			this.mailHistoryRepository.save(mailHistory);
		}

		return true;
	}

	public void send(String receiver, String sender, String subject, String html) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setFrom(sender);
			mimeMessageHelper.setTo(receiver);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(html, true);

			javaMailSender.send(mimeMessage);
			log.info("[MAIL_SEND] FROM: [{}] , TO: [{}] , TITLE: [{}]", sender, receiver, subject);
		} catch (Exception e) {
			log.error("failed to send email", e);
			throw new RuntimeException(e);
		}
	}

	public void sendAttachment(
		String receiver, String sender, String subject, String html, byte[] bytes
	) throws MessagingException, IOException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setFrom(sender);
			mimeMessageHelper.setTo(receiver);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(html, true);
			mimeMessageHelper.setEncodeFilenames(false);

			String fileName = "버넥트 솔루션 도입 ROI 측정 결과.pdf";

			DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf", fileName);
			mimeMessageHelper.addAttachment(fileName, dataSource);

			javaMailSender.send(mimeMessage);
			log.info("[ATTACHMENT_MAIL_SEND] FROM: [{}] , TO: [{}] , TITLE: [{}]", sender, receiver, subject);
		} catch (Exception e) {
			log.error("failed to send email", e);
			throw new RuntimeException(e);
		}
	}
}
