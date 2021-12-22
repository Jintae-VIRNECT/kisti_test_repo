package com.virnect.uaa.infra;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.context.Context;

import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.email.context.MailMessageContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class EmailServiceTest {
	String RECEIVE_EMAIL = "sky456139@virnect.com";

	@Autowired
	EmailService emailService;

	@Test
	@Order(1)
	@DisplayName("국문 이메일 인증 이메일 발송 테스트")
	public void mailIdAuthorizationEmailTest() {
		// send email authentication email
		long duration = Duration.ofMinutes(30).getSeconds();
		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(duration);

		Context context = new Context();
		context.setVariable("email", RECEIVE_EMAIL);
		context.setVariable("code", RandomStringUtils.randomNumeric(6));
		context.setVariable("expiredDate", zonedDateTime);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("email_check")
			.titleMessageSourceName("MAIL_TITLE_OF_REGISTER_EMAIL_CHECK")
			.to(RECEIVE_EMAIL)
			.locale(Locale.KOREA)
			.build();
		emailService.send(mailMessageContext);
	}

	@Test
	@Order(2)
	@DisplayName("영문 이메일 인증 이메일 발송 테스트")
	public void mailIdAuthorizationEmailEnglishTest() {
		// send email authentication email
		long duration = Duration.ofMinutes(30).getSeconds();
		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(duration);

		Context context = new Context();
		context.setVariable("email", RECEIVE_EMAIL);
		context.setVariable("code", RandomStringUtils.randomNumeric(6));
		context.setVariable("expiredDate", zonedDateTime);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("email_check")
			.titleMessageSourceName("MAIL_TITLE_OF_REGISTER_EMAIL_CHECK")
			.to(RECEIVE_EMAIL)
			.locale(Locale.ENGLISH)
			.build();
		emailService.send(mailMessageContext);
	}

	@Test
	@Order(3)
	@DisplayName("국문 회원 가입 축하 메일 발송 테스트")
	public void welcomeEmailTest() {
		Context context = new Context();
		context.setVariable("email", "practice1356@gmail.com");
		context.setVariable("name", "장정현");
		context.setVariable("nickname", "JohnMark");

		LocalDateTime userRegisterDate = LocalDateTime.now();
		String registerDate = String.format(
			"%s GMT+9:00",
			userRegisterDate.plusHours(9).format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
		);
		context.setVariable("registerDate", registerDate);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("welcome")
			.titleMessageSourceName("MAIL_TITLE_OF_REGISTER_SUCCESS")
			.locale(Locale.KOREA)
			.to(RECEIVE_EMAIL)
			.build();

		emailService.send(mailMessageContext);
	}

	@Test
	@Order(4)
	@DisplayName("영문 회원 가입 축하 메일 발송 테스트")
	public void welcomeEmailEnglishTest() {
		Context context = new Context();
		context.setVariable("email", "practice1356@gmail.com");
		context.setVariable("name", "장정현");
		context.setVariable("nickname", "JohnMark");

		LocalDateTime userRegisterDate = LocalDateTime.now();
		String registerDate = String.format(
			"%s GMT+9:00",
			userRegisterDate.plusHours(9).format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
		);
		context.setVariable("registerDate", registerDate);

		MailMessageContext mailMessageContextEng = MailMessageContext.builder()
			.templateContext(context)
			.templateName("welcome")
			.titleMessageSourceName("MAIL_TITLE_OF_REGISTER_SUCCESS")
			.locale(Locale.ENGLISH)
			.to(RECEIVE_EMAIL)
			.build();

		emailService.send(mailMessageContextEng);
	}

	@Test
	@Order(5)
	@DisplayName("국문 비밀번호 변경 인증 코드 메일 발송 테스트")
	public void passwordResetAuthorizationCodeEmailTest() {
		String authCode = RandomStringUtils.randomAlphanumeric(6);
		System.out.println("authCode = " + authCode);
		Duration expireDuration = Duration.ofMinutes(30);

		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(expireDuration.getSeconds());

		Context context = new Context();
		context.setVariable("code", authCode);
		context.setVariable("expiredDate", zonedDateTime);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("password_change_code")
			.titleMessageSourceName("MAIL_TITLE_OF_PASSWORD_CHANGE_AUTH_CODE")
			.to(RECEIVE_EMAIL)
			.locale(Locale.KOREA)
			.build();

		emailService.send(mailMessageContext);
	}

	@Test
	@Order(6)
	@DisplayName("영문 비밀번호 변경 인증 코드 메일 발송 테스트")
	public void passwordResetAuthorizationCodeEmailEnglishTest() {
		String authCode = RandomStringUtils.randomAlphanumeric(6);
		System.out.println("authCode = " + authCode);
		Duration expireDuration = Duration.ofMinutes(30);

		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(expireDuration.getSeconds());

		Context context = new Context();
		context.setVariable("code", authCode);
		context.setVariable("expiredDate", zonedDateTime);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("password_change_code")
			.titleMessageSourceName("MAIL_TITLE_OF_PASSWORD_CHANGE_AUTH_CODE")
			.to(RECEIVE_EMAIL)
			.locale(Locale.ENGLISH)
			.build();

		emailService.send(mailMessageContext);
	}
}