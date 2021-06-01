package com.virnect.message.application.mail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

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
	private final AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient;
	private final SpringTemplateEngine springTemplateEngine;
	private final MailHistoryRepository mailHistoryRepository;
	private final FileService fileService;

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
			send(receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml());

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

			sendAttachment(
				receiver, MailSender.MASTER.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml(), bytes,
				FilenameUtils.getName(mailSendRequest.getMultipartFile())
			);

			log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

			this.mailHistoryRepository.save(mailHistory);
		}

		return true;
	}

	public void sendTemplateMail(
		String sender, List<String> receivers, String subject, String mailTemplate, Context context
	) {
		String html = this.springTemplateEngine.process(mailTemplate, context);
		Message message = new Message()
			.withSubject(createContent(subject))
			.withBody(new Body().withHtml(createContent(html)));

		for (String receiver : receivers) {
			SendEmailRequest sendEmailRequest = new SendEmailRequest()
				.withSource(sender)
				.withDestination(new Destination().withToAddresses(receiver))
				.withMessage(message);
			this.amazonSimpleEmailServiceAsyncClient.sendEmailAsync(sendEmailRequest);
		}
	}

	private Content createContent(String data) {
		return new Content().withCharset("UTF-8").withData(data);
	}

	public void send(String receiver, String sender, String subject, String html) {
		Message message = new Message()
			.withSubject(createContent(subject))
			.withBody(new Body().withHtml(createContent(html)));

		SendEmailRequest sendEmailRequest = new SendEmailRequest()
			.withSource(sender)
			.withDestination(new Destination().withToAddresses(receiver))
			.withMessage(message);
		this.amazonSimpleEmailServiceAsyncClient.sendEmailAsync(sendEmailRequest);

	}

	public void sendAttachment(
		String receiver, String sender, String subject, String html, byte[] bytes, String fileName
	) throws MessagingException, IOException {

		Session session = Session.getDefaultInstance(new Properties());

		MimeMessage message = new MimeMessage(session);
		message.setSubject(subject, "UTF-8");
		message.setFrom(new InternetAddress(sender));
		message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(receiver));

		MimeMultipart msg_body = new MimeMultipart("alternative");
		MimeBodyPart wrap = new MimeBodyPart();
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(html, "text/html; charset=UTF-8");
		msg_body.addBodyPart(htmlPart);
		wrap.setContent(msg_body);

		MimeMultipart msg = new MimeMultipart("mixed");
		message.setContent(msg);
		msg.addBodyPart(wrap);

		//첨부파일
		File convertFile = new File("버넥트 솔루션 도입 ROI 측정 결과.pdf");
		if (convertFile.createNewFile()) {
			FileOutputStream fos = new FileOutputStream(convertFile);
			fos.write(bytes);
			fos.close();
		}
		DataSource dataSource = new ByteArrayDataSource(bytes, "application/octet-stream", "버넥트 솔루션 도입 ROI 측정 결과.pdf");
		BodyPart bodyPart = new MimeBodyPart();
		bodyPart.setDataHandler(new DataHandler(dataSource));
		bodyPart.setFileName("버넥트 솔루션 도입 ROI 측정 결과.pdf");
		msg.addBodyPart(bodyPart);

		try {
			PrintStream out = System.out;
			message.writeTo(out);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage =
				new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

			SendRawEmailRequest rawEmailRequest =
				new SendRawEmailRequest(rawMessage);

			amazonSimpleEmailServiceAsyncClient.sendRawEmail(rawEmailRequest);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		convertFile.delete();
	}

}
