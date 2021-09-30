package com.virnect.uaa.infra.email.context;

import java.util.Locale;

import org.thymeleaf.context.Context;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MailMessageContext {
	private final Context templateContext;
	private final String templateName;
	private final String titleMessageSourceName;
	private final String to;
	private Locale locale;

	@Builder
	public MailMessageContext(
		Context templateContext, String templateName, String titleMessageSourceName, String to, Locale locale
	) {
		this.templateContext = templateContext;
		this.templateName = templateName;
		this.titleMessageSourceName = titleMessageSourceName;
		this.to = to;
		this.locale = locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean isSupportLanguageLocale() {
		return locale.getLanguage().equalsIgnoreCase("ko") ||
							locale.getLanguage().equalsIgnoreCase("en");
	}

	@Override
	public String toString() {
		return "MailContext{" +
			"templateContext=" + templateContext +
			", templateName='" + templateName + '\'' +
			", titleMessageSourceName='" + titleMessageSourceName + '\'' +
			", to='" + to + '\'' +
			", locale=" + locale +
			'}';
	}
}
