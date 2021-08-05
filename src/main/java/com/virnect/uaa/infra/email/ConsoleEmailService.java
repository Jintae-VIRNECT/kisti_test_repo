package com.virnect.uaa.infra.email;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.email.context.MailMessageContext;
import com.virnect.uaa.infra.email.context.MailTemplateProcessor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description console email service for development
 * @since 2020.03.25
 */
@Slf4j
@Profile("test")
@Component
@RequiredArgsConstructor
public class ConsoleEmailService implements EmailService {
	private final MailTemplateProcessor templateProcessor;

	@Override
	public void send(MailMessageContext mailMessageContext) {
		String title = templateProcessor.resolveMailTitleMessageSource(mailMessageContext);
		String messageBody = templateProcessor.compileMailTemplate(mailMessageContext);

		log.info(" - [CONSOLE_MAIL][from] :: [{}]", getSystemEmail());
		log.info(" - [CONSOLE_MAIL][TO] :: [{}]", mailMessageContext.getTo());
		log.info(" - [CONSOLE_MAIL][TITLE] :: [{}]", title);
		log.info(" - [CONSOLE_MAIL][BODY] ::\n{}", messageBody);
	}
}
