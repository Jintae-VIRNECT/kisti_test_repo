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
			log.warn(
				"Request locale is not support : [{}] , is change to default locale(EN)", messageContext.getLocale());
			messageContext.setLocale(Locale.ENGLISH);
		}
		String message = messageSource.getMessage(
			messageContext.getTitleMessageSourceName(), null, messageContext.getLocale());
		log.debug("[MessageSource] key:[{}] value:[{}]", messageContext.getTitleMessageSourceName(), message);
		return message;
	}

	public String compileMailTemplate(MailMessageContext mailMessageContext) {
		String mailTemplateFilePath = resolveTemplateMailPath(mailMessageContext);
		log.debug("[TemplateCompileResult][TemplatePath] - [{}]", mailTemplateFilePath);
		String result = templateEngine.process(mailTemplateFilePath, mailMessageContext.getTemplateContext());
		log.debug("[TemplateCompileResult] - [{}]", result);
		return result;
	}

	private String resolveTemplateMailPath(MailMessageContext messageContext) {
		if (!messageContext.isSupportLanguageLocale()) {
			log.warn(
				"Request locale is not support : [{}] , is change to default locale(EN)", messageContext.getLocale());
			messageContext.setLocale(Locale.ENGLISH);
		}
		String templatePath = String.format(
			"%s/%s", messageContext.getLocale().getLanguage(), messageContext.getTemplateName());
		log.debug("[TemplatePath] - [{}]", templatePath);
		return templatePath;
	}

}
