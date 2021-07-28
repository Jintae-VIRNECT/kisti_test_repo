package com.virnect.uaa.infra.email;

import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.email.context.MailMessageContext;
import com.virnect.uaa.infra.email.context.MailTemplateProcessor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Html Email Message Send Service
 * @since 2020.03.25
 */

@Slf4j
@Profile(value = "!test")
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {
	private final MailTemplateProcessor templateProcessor;
	private final JavaMailSender javaMailSender;

	@Override
	@Async("threadPoolTaskExecutor")
	public void send(MailMessageContext messageContext) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			String title = templateProcessor.resolveMailTitleMessageSource(messageContext);
			String messageBody = templateProcessor.compileMailTemplate(messageContext);

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setFrom(getSystemEmail());
			mimeMessageHelper.setTo(messageContext.getTo());
			mimeMessageHelper.setSubject(title);
			mimeMessageHelper.setText(messageBody, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			log.error("failed to send email", e);
			throw new RuntimeException(e);
		}
	}
}
