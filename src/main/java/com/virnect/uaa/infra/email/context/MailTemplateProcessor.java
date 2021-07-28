package com.virnect.uaa.infra.email.context;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailTemplateProcessor {
	private final SpringTemplateEngine templateEngine;
	private final MessageSource messageSource;

	public String resolveMailTitleMessageSource(MailMessageContext messageContext) {
		if (!messageContext.isSupportLanguageLocale()) {
			log.info(
				"Request locale is not support : [{}] , is change to default locale(EN)", messageContext.getLocale());
			messageContext.setLocale(Locale.ENGLISH);
		}
		return messageSource.getMessage(messageContext.getTitleMessageSourceName(), null, messageContext.getLocale());
	}

	public String compileMailTemplate(MailMessageContext mailMessageContext) {
		String mailTemplateFilePath = resolveTemplateMailPath(mailMessageContext);
		return templateEngine.process(mailTemplateFilePath, mailMessageContext.getTemplateContext());
	}

	private String resolveTemplateMailPath(MailMessageContext messageContext) {
		if (!messageContext.isSupportLanguageLocale()) {
			log.info(
				"Request locale is not support : [{}] , is change to default locale(EN)", messageContext.getLocale());
			messageContext.setLocale(Locale.ENGLISH);
		}
		return String.format("%s/%s", messageContext.getLocale().getLanguage(), messageContext.getTemplateName());
	}

}
