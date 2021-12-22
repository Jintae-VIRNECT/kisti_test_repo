package com.virnect.uaa.domain.auth.account.application.signup.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.email.context.MailMessageContext;
import com.virnect.uaa.infra.rest.billing.PayService;
import com.virnect.uaa.infra.rest.billing.dto.CouponRegisterResponse;
import com.virnect.uaa.infra.rest.billing.dto.PayletterApiResponse;

@Slf4j
@Profile(value = {"!onpremise"})
@Service
@RequiredArgsConstructor
public class DefaultSuccessHandler implements SignUpSuccessHandler {
	private final PayService payService;
	private final EmailService emailService;

	@Override
	public void signUpSuccess(
		User user, HttpServletRequest request, Locale locale
	) {
		log.info("SignUpSuccess - Sign UP Welcome Coupon");
		assignWelcomeCoupon(user);
		log.info("SignUpSuccess - Sign UP Welcome Email");
		sendSignUpWelcomeEmail(user, locale);
	}

	private void assignWelcomeCoupon(User user) {
		ResponseEntity<PayletterApiResponse<CouponRegisterResponse>> couponAssignResponseEntity = payService.welcomeEventCouponRegister(
			user.getEmail(), user.getName(), user.getId());

		if (couponAssignResponseEntity == null) {
			return;
		}

		log.info("[USER_EVENT_COUPON_REGISTER_RESPONSE] - [{}]", couponAssignResponseEntity);
	}

	public void sendSignUpWelcomeEmail(User user, Locale locale) {
		Context context = new Context();
		context.setVariable("email", user.getEmail());
		context.setVariable("name", user.getName());
		context.setVariable("nickname", user.getNickname());

		LocalDateTime userRegisterDate = user.getCreatedDate();
		String registerDate = String.format(
			"%s GMT+9:00",
			userRegisterDate.plusHours(9).format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
		);
		context.setVariable("registerDate", registerDate);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("welcome")
			.titleMessageSourceName("MAIL_TITLE_OF_REGISTER_SUCCESS")
			.locale(locale)
			.to(user.getEmail())
			.build();

		emailService.send(mailMessageContext);
	}
}
