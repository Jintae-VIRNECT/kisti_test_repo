package com.virnect.uaa.domain.user.application;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
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
@RequiredArgsConstructor
public class UserInformationFindServiceImpl implements UserInformationFindService {
	private final UserRepository userRepository;
	private final UserPasswordAuthCodeRepository userPasswordAuthCodeRepository;

	private final EmailService emailService;

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
		String authCode = RandomStringUtils.random(6);
		Duration expireDuration = Duration.ofMinutes(30);

		// Create password initialized authorization code
		PasswordInitAuthCode passwordResetCode = PasswordInitAuthCode.of(user, authCode, expireDuration.getSeconds());
		userPasswordAuthCodeRepository.save(passwordResetCode);

		// Send password reset authorization code mail to request user
		sendPasswordResetAuthorizationCodeMail(locale, user, authCode, expireDuration);

		return new UserPasswordFindAuthCodeResponse(true, user.getEmail());
	}

	private void sendPasswordResetAuthorizationCodeMail(Locale locale, User user, String authCode, Duration expireDuration) {
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

	@Override
	public UserPasswordFindCodeCheckResponse verifyPasswordResetCode(
		UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest
	) {
		return null;
	}

	@Override
	public UserPasswordChangeResponse renewalPreviousPassword(
		UserPasswordChangeRequest passwordChangeRequest
	) {
		return null;
	}

}
