package com.virnect.uaa.domain.auth.account.application.signup.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.rest.billing.PayService;

@Slf4j
@Profile(value = {"!onpremise"})
@Service
@RequiredArgsConstructor
public class DefaultSuccessHandler implements SignUpSuccessHandler {
	private final PayService payService;
	private final MessageSource messageSource;
	private final SpringTemplateEngine templateEngine;
	private final EmailService emailService;

	@Override
	public void signUpSuccess(
		User user, HttpServletRequest request, Locale locale
	) {
		log.info("SignUpSuccess - Sign UP Welcome Coupon");
		payService.eventCouponRegisterToNewUser(user.getEmail(), user.getName(), user.getId());
		log.info("SignUpSuccess - Sign UP Welcome Email");
		sendSignUpWelcomeEmail(user, locale);
	}

	public void sendSignUpWelcomeEmail(User user, Locale locale) {
		Context context = new Context();
		context.setVariable("email", user.getEmail());
		context.setVariable("name", user.getName());
		context.setVariable("nickname", user.getNickname());

		LocalDateTime userRegisterDate = user.getCreatedDate();
		String registerDate = String.format(
			"%s GMT+9:00",
			userRegisterDate.plusHours(9).format(DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm"))
		);
		context.setVariable("registerDate", registerDate);

		String mailTitle = messageSource.getMessage("MAIL_TITLE_OF_REGISTER_SUCCESS", null, locale);
		String mailTemplatePath = String.format("%s/register/welcome", locale.getLanguage());
		log.info("mailTemplatePath: {}", mailTemplatePath);
		String message = templateEngine.process(mailTemplatePath, context);

		// EmailMessage registrationSuccessMail = new EmailMessage();
		// registrationSuccessMail.setSubject(mailTitle);
		// registrationSuccessMail.setTo(user.getEmail());
		// registrationSuccessMail.setMessage(message);
		//
		// log.info(
		// 	"[SEND_REGISTRATION_SUCCESS_MAIL] - title: {} , to: {}",
		// 	registrationSuccessMail.getSubject(),
		// 	registrationSuccessMail.getTo()
		// );
		//
		// emailService.send(registrationSuccessMail);
	}
}
