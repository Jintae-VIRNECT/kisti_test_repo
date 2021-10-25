package com.virnect.uaa.domain.user.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dao.UserPasswordAuthCodeRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.PasswordInitAuthCode;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.EmailFindRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeRequest;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindAuthCodeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindCodeCheckResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.MaskingUtil;
import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.email.context.MailMessageContext;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserInformationFindServiceImpl implements UserInformationFindService {
	private final UserRepository userRepository;
	private final UserPasswordAuthCodeRepository userPasswordAuthCodeRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserEmailFindResponse findUserEmail(
		EmailFindRequest emailFindRequest
	) {
		List<User> users = userRepository.findUserByNameAndRecoveryEmailOrInternationalNumberAndMobile(
			emailFindRequest.getFullName(), emailFindRequest.getRecoveryEmail(), emailFindRequest.getMobile()
		);

		if (users.isEmpty()) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND);
		}

		List<UserEmailFindInfoResponse> findEmailMaskingResult = users.stream().map(user -> {
			UserEmailFindInfoResponse userEmailFindInfoResponse = new UserEmailFindInfoResponse();
			userEmailFindInfoResponse.setEmail(MaskingUtil.emailMasking(user.getEmail(), 4));
			userEmailFindInfoResponse.setSignUpDate(user.getCreatedDate().toLocalDate());
			return userEmailFindInfoResponse;
		}).collect(Collectors.toList());

		return new UserEmailFindResponse(findEmailMaskingResult);
	}

	@Transactional
	@Override
	public UserPasswordFindAuthCodeResponse sendPasswordResetEmail(
		UserPasswordFindAuthCodeRequest passwordAuthCodeRequest,
		Locale locale
	) {
		User user = userRepository.findByEmail(passwordAuthCodeRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		// Delete previous duplicated authentication request
		previousDuplicatedAuthenticationRequestCancelProcess(user);

		// Generate authentication information
		String authCode = RandomStringUtils.randomNumeric(6);
		Duration expireDuration = Duration.ofMinutes(30);

		// Create password initialized authorization code
		PasswordInitAuthCode passwordResetCode = PasswordInitAuthCode.of(user, authCode, expireDuration.getSeconds());
		userPasswordAuthCodeRepository.save(passwordResetCode);

		// Send password reset authorization code mail to request user
		sendPasswordResetAuthorizationCodeMail(locale, user, authCode, expireDuration);

		return new UserPasswordFindAuthCodeResponse(true, user.getEmail());
	}

	private void sendPasswordResetAuthorizationCodeMail(
		Locale locale, User user, String authCode, Duration expireDuration
	) {
		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(expireDuration.getSeconds());

		Context context = new Context();
		context.setVariable("code", authCode);
		context.setVariable("expiredDate", zonedDateTime);

		MailMessageContext mailMessageContext = MailMessageContext.builder()
			.templateContext(context)
			.templateName("password_change_code")
			.titleMessageSourceName("MAIL_TITLE_OF_PASSWORD_CHANGE_AUTH_CODE")
			.to(user.getEmail())
			.locale(locale)
			.build();

		emailService.send(mailMessageContext);
	}

	private void previousDuplicatedAuthenticationRequestCancelProcess(User user) {
		boolean isDuplicatedRequest = userPasswordAuthCodeRepository.existsById(user.getEmail());
		if (isDuplicatedRequest) {
			userPasswordAuthCodeRepository.deleteById(user.getEmail());
		}
	}

	@Transactional
	@Override
	public UserPasswordFindCodeCheckResponse verifyPasswordResetCode(
		UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest
	) {
		PasswordInitAuthCode passwordInitAuthCode = userPasswordAuthCodeRepository.findById(
				authCodeCheckRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_PASSWORD_INIT_CODE_NOT_FOUND));

		log.info(
			"Password Initialize Info Check : REQ:[{}] SERVER:[{}] -> RESULT:[{}]",
			authCodeCheckRequest.getCode(),
			passwordInitAuthCode.getCode(),
			authCodeCheckRequest.getCode().equals(passwordInitAuthCode.getCode())
		);

		if (!passwordInitAuthCode.getCode().equals(authCodeCheckRequest.getCode())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_PASSWORD_INIT_CODE_NOT_FOUND);
		}

		userPasswordAuthCodeRepository.deleteById(authCodeCheckRequest.getEmail());

		return new UserPasswordFindCodeCheckResponse(
			passwordInitAuthCode.getUuid(), passwordInitAuthCode.getEmail(), true);
	}

	@Transactional
	@Override
	public UserPasswordChangeResponse renewalPreviousPassword(
		UserPasswordChangeRequest passwordChangeRequest
	) {
		User user = userRepository.findByEmail(passwordChangeRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		// Check for duplicated password with previous password
		if (passwordEncoder.matches(passwordChangeRequest.getPassword(), user.getPassword())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE_DUPLICATE);
		}

		// Check Password Format
		if (!passwordChangeRequest.getPassword().matches("[a-zA-Z0-9\\.!@#$%가-힣ㄱ-ㅎ]+")) {
			log.info("password format not matched REGEXP of '[a-zA-Z0-9\\.!@#$%]+'");
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE);
		}

		String encodedNewPassword = passwordEncoder.encode(passwordChangeRequest.getPassword());
		user.setPassword(encodedNewPassword);
		user.setPasswordUpdateDate(LocalDateTime.now());

		// password invalid account lock free
		if (!user.isAccountNonLocked()) {
			log.info(
				"[USER][INACTIVE_ACCOUNT_LOCK] - Email:[{}] Name:[{}]", user.getEmail(),
				user.getName()
			);
			user.setAccountNonLocked(true);
		}
		userRepository.save(user);
		return new UserPasswordChangeResponse(true, user.getEmail(), user.getPasswordUpdateDate());
	}

}
