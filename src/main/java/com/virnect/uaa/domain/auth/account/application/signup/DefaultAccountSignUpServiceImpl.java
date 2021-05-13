package com.virnect.uaa.domain.auth.account.application.signup;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.application.UserAccessLogService;
import com.virnect.uaa.domain.auth.account.dao.EmailAuthorizationRepository;
import com.virnect.uaa.domain.auth.account.domain.EmailAuth;
import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.domain.user.dao.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.global.security.token.JwtTokenProvider;
import com.virnect.uaa.infra.email.EmailMessage;
import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.file.Default;
import com.virnect.uaa.infra.file.FileService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAccountSignUpServiceImpl implements AccountSignUpService {
	private final FileService fileService;
	private final PasswordEncoder passwordEncoder;
	private final SecessionUserRepository secessionUserRepository;
	private final UserRepository userRepository;
	private final EmailAuthorizationRepository emailAuthorizationRepository;
	private final SignUpSuccessHandler signUpSuccessHandler;
	private final UserAccessLogService userAccessLogService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MessageSource messageSource;
	private final SpringTemplateEngine templateEngine;
	private final EmailService emailService;

	@Transactional
	@Override
	public OAuthTokenResponse signUp(
		RegisterRequest registerRequest,
		HttpServletRequest request,
		Locale locale
	) {
		// Signup Session Code Check
		signupSessionCodeValidate(registerRequest.getEmail(), registerRequest.getSessionCode());

		// Check SignUp User Email Unique
		signupEmailDuplicateCheck(registerRequest.getEmail());

		// create register user
		User user = createNewUser(registerRequest);

		// save new user
		userRepository.save(user);

		// parse client user agent
		ClientGeoIPInfo clientGeoIPInfo = userAccessLogService.saveUserAccessLogInformation(user, request);

		// generate oauth token
		OAuthTokenResponse oauthTokenFromSignup = getOauthTokenFromSignup(user, clientGeoIPInfo);

		// Do After Signup Success
		signUpSuccessHandler.signUpSuccess(user, request, locale);

		return oauthTokenFromSignup;
	}

	@Override
	public EmailAuthResponse emailAuthentication(
		EmailAuthRequest emailAuthRequest,
		Locale locale
	) {
		// 탈퇴 회원의 이메일 정보인지 확인
		checkSecessionUserEmail(emailAuthRequest.getEmail());

		// 이미 가입된 사용자가 있는지 확인
		emailDuplicateCheck(emailAuthRequest);

		// 기존 인증 요청이 있는지 확인
		authorizationRequestDuplicateCheck(emailAuthRequest);

		// 인증 이메일 전송
		sendAuthenticationEmail(emailAuthRequest, locale);

		return new EmailAuthResponse(true);
	}

	@Override
	public EmailVerificationResponse emailAuthCodeVerification(
		String email,
		String code
	) {
		return null;
	}

	private void checkSecessionUserEmail(String email) {
		boolean isSecessionUserEmail = secessionUserRepository.existsByEmail(email);
		if (isSecessionUserEmail) {
			log.info("[EMAIL_AUTHENTICATION] [SECESSION_USER] : [{}]", email);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_SECESSION_USER_EMAIL);
		}
	}

	private void emailDuplicateCheck(EmailAuthRequest emailAuthRequest) {
		boolean hasDuplicatedEmail = userRepository.existsByEmail(emailAuthRequest.getEmail());

		// Check email is Duplicate
		if (hasDuplicatedEmail) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_DUPLICATED_EMAIL);
		}
	}

	/**
	 * 이메일 인증 요청 중복 여부 검사
	 *
	 * @param emailAuthRequest - 이메일 인증 요청 정보
	 */
	private void authorizationRequestDuplicateCheck(EmailAuthRequest emailAuthRequest) {
		boolean hasPreviousRequest = emailAuthorizationRepository
			.existsById(emailAuthRequest.getEmail());

		// if have previous request
		if (hasPreviousRequest) {
			// delete previous request
			emailAuthorizationRepository.deleteById(emailAuthRequest.getEmail());
		}
	}

	/**
	 * 이메일 인증 메일 전송 처리
	 *
	 * @param emailAuthRequest - 이메일 인증 요청 정보
	 * @param locale
	 */
	private void sendAuthenticationEmail(EmailAuthRequest emailAuthRequest, Locale locale) {
		String code = RandomStringUtils.randomNumeric(6);
		String sessionCode = UUID.randomUUID().toString().replace("-", "");
		long duration = Duration.ofMinutes(30).getSeconds();

		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		ZonedDateTime zonedDateTime = ZonedDateTime.now(seoulZoneOffset).plusSeconds(duration);

		EmailAuth emailAuth = EmailAuth.builder()
			.email(emailAuthRequest.getEmail())
			.code(code)
			.sessionCode(sessionCode)
			.expireDate(duration)
			.build();

		// save new authentication request on redis
		emailAuthorizationRepository.save(emailAuth);

		// send email authentication email
		Context context = new Context();
		context.setVariable("email", emailAuthRequest.getEmail());
		context.setVariable("code", code);
		context.setVariable("expiredDate", zonedDateTime);

		String mailTitle = messageSource.getMessage("MAIL_TITLE_OF_REGISTER_EMAIL_CHECK", null, locale);
		String mailTemplatePath = String.format("%s/register/email_check", locale.getLanguage());
		log.info("mailTemplatePath: {}", mailTemplatePath);
		String message = templateEngine.process(mailTemplatePath, context);

		EmailMessage registerEmailAuthenticationMail = new EmailMessage();
		registerEmailAuthenticationMail.setSubject(mailTitle);
		registerEmailAuthenticationMail.setTo(emailAuthRequest.getEmail());
		registerEmailAuthenticationMail.setMessage(message);

		log.info(
			"[SEND_EMAIL_CHECK_EMAIL] - title: {} , to: {}",
			registerEmailAuthenticationMail.getSubject(),
			registerEmailAuthenticationMail.getTo()
		);

		emailService.sendEmail(registerEmailAuthenticationMail);
	}

	public void signupSessionCodeValidate(String email, String sessionCode) {
		EmailAuth emailAuth = emailAuthorizationRepository.findById(email)
			.orElseThrow(() -> {
					log.error("EmailAuthentication Information Not Found. Search by [{}]", email);
					return new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_SESSION_EXPIRE);
				}
			);

		if (!emailAuth.getSessionCode().equals(sessionCode) || !emailAuth.getEmail().equals(email)) {
			log.error("Register Email or SessionCode is not matched.");
			log.error("Register Request Email: [{}] , Register Request SessionCode: [{}]", email, sessionCode);
			log.error("emailAuth: [{}]", emailAuth);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_AUTHENTICATION);
		}
	}

	public void signupEmailDuplicateCheck(String email) {
		boolean isDuplicated = userRepository.existsByEmail(email);
		if (isDuplicated) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_DUPLICATED_EMAIL);
		}
	}

	public User createNewUser(RegisterRequest registerRequest) {
		String profileUrl = getUserProfileUrlFromProfileImage(registerRequest.getProfile());
		String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
		return User.BySignUpUserBuilder()
			.registerRequest(registerRequest)
			.encodedPassword(encodedPassword)
			.profileUrl(profileUrl)
			.build();
	}

	public String getUserProfileUrlFromProfileImage(MultipartFile profile) {
		// return default image url
		if (profile == null) {
			return Default.USER_PROFILE.getValue();
		}
		try {
			return fileService.upload(profile);
		} catch (IOException e) {
			log.error("profile image upload fail.");
			log.error(e.getMessage(), e);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER);
		}
	}

	public OAuthTokenResponse getOauthTokenFromSignup(User user, ClientGeoIPInfo clientGeoIPInfo) {
		String accessTokenJwtId = UUID.randomUUID().toString();
		String refreshTokenJwtId = UUID.randomUUID().toString();
		String accessToken = jwtTokenProvider.createAccessToken(user, accessTokenJwtId, clientGeoIPInfo);
		String refreshToken = jwtTokenProvider.createRefreshToken(
			user, refreshTokenJwtId, accessTokenJwtId, clientGeoIPInfo);
		OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
		oAuthTokenResponse.setAccessToken(accessToken);
		oAuthTokenResponse.setRefreshToken(refreshToken);
		oAuthTokenResponse.setExpireIn(jwtTokenProvider.getAccessTokenExpire());
		oAuthTokenResponse.setPasswordInitialized(true);
		return oAuthTokenResponse;
	}
}
