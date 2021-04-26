package com.virnect.uaa.domain.auth.account.application;

import static java.util.Objects.*;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.TextCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.BlockTokenRepository;
import com.virnect.uaa.domain.auth.account.dao.EmailAuthorizationRepository;
import com.virnect.uaa.domain.auth.account.dao.LoginAttemptRepository;
import com.virnect.uaa.domain.auth.account.dao.UserOTPRepository;
import com.virnect.uaa.domain.auth.account.domain.BlockReason;
import com.virnect.uaa.domain.auth.account.domain.BlockToken;
import com.virnect.uaa.domain.auth.account.domain.EmailAuth;
import com.virnect.uaa.domain.auth.account.domain.LoginAttempt;
import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthenticationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;
import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.error.exception.LoginFailException;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.domain.auth.account.event.account.AccountLockEvent;
import com.virnect.uaa.domain.auth.device.application.DeviceAuthenticationService;
import com.virnect.uaa.domain.user.dao.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.global.common.ClientUserAgentInformationParser;
import com.virnect.uaa.global.config.token.TokenProperty;
import com.virnect.uaa.global.security.token.JwtPayload;
import com.virnect.uaa.global.security.token.JwtTokenProvider;
import com.virnect.uaa.infra.email.EmailMessage;
import com.virnect.uaa.infra.email.EmailService;
import com.virnect.uaa.infra.rest.user.UserRestService;
import com.virnect.uaa.infra.rest.user.dto.UserInfoResponse;
import com.virnect.uaa.infra.rest.workspace.WorkspaceRestService;

@Slf4j
@Profile(value = "onpremise")
@Service
@RequiredArgsConstructor
public class OnPremiseUserAuthenticationServiceImpl implements UserAuthenticationService {
	private static final String REMEMBER_ME_COOKIE = "remember-me";
	private static final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserRestService userRestService;
	private final EmailService emailService;
	private final DeviceAuthenticationService deviceAuthenticationService;
	private final UserAccessLogRepository userAccessLogRepository;
	private final SecessionUserRepository secessionUserRepository;
	private final EmailAuthorizationRepository emailAuthorizationRepository;
	private final BlockTokenRepository blockTokenRepository;
	private final LoginAttemptRepository loginAttemptRepository;
	private final UserOTPRepository userOTPRepository;
	private final GoogleAuthenticator googleAuthenticator;
	private final SpringTemplateEngine templateEngine;
	private final TokenProperty tokenProperty;
	private final ObjectMapper objectMapper;
	private final MessageSource messageSource;
	private final WorkspaceRestService workspaceRestService;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ClientUserAgentInformationParser clientUserAgentInformationParser;

	/**
	 * 로그인 요청 처리
	 *
	 * @param loginRequest - 로그인 요청 데이터
	 * @param request      - 로그인 요청 http 정보
	 * @param response
	 * @return
	 */
	@Transactional
	public OAuthTokenResponse loginHandler(
		LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response
	) {
		User user = userRepository.findByEmail(loginRequest.getEmail())
			.orElseThrow(() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGIN));

		if (loginRequest.isRememberMe() && loginRequest.getPassword() == null && request.getCookies() != null) {
			Cookie rememberMeCookie = Stream.of(request.getCookies())
				.filter(cookie -> cookie.getName().equals(REMEMBER_ME_COOKIE))
				.findFirst()
				.orElseThrow(() -> new UserAuthenticationServiceException(
					AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER));
			String userRememberMeToken = rememberMeCookie.getValue();
			if (!jwtTokenProvider.isValidToken(userRememberMeToken)) {
				throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
			}
			log.info("[AUTO_LOGIN] EMAIL:{} , TOKEN: {}", user.getEmail(), userRememberMeToken);
			loginAttemptRepository.deleteById(user.getEmail());
			OAuthTokenResponse oAuthTokenResponse = getLoginResponse(user, request);
			return oAuthTokenResponse;
		}

		// 계정 상태 체크
		accountValidation(user);

		if (loginRequest.getPassword() == null) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			// throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGIN);
			loginAttemptCheck(user);
		}

		loginAttemptRepository.deleteById(user.getEmail());
		OAuthTokenResponse oAuthTokenResponse = getLoginResponse(user, request);

		if (loginRequest.isRememberMe()) {
			Cookie cookie = new Cookie(REMEMBER_ME_COOKIE, oAuthTokenResponse.getRefreshToken());
			cookie.setDomain(tokenProperty.getCookieDomain());
			cookie.setHttpOnly(true);
			cookie.setSecure(true);
			cookie.setMaxAge((int)TimeUnit.DAYS.toSeconds(tokenProperty.getCookieMaxAgeDay()));
			cookie.setPath(tokenProperty.getCookiePath());
			response.addCookie(cookie);
			log.info(
				"[AUTO_LOGIN][CREATE COOKIE]: {} =>[{}] , {}, {}, {}", cookie.getName(), cookie.getValue(),
				cookie.getDomain(), cookie.getMaxAge(), cookie.getPath()
			);
		}
		return oAuthTokenResponse;
	}

	/**
	 * 로그인 시도 검사
	 *
	 * @param user - 로그인 시도 계정
	 */
	private void loginAttemptCheck(User user) {
		Optional<LoginAttempt> loginAttemptData = loginAttemptRepository.findById(user.getEmail());

		// 만약 로그인 시도 데이터가 없다면
		if (!loginAttemptData.isPresent()) {
			LoginAttempt newLoginAttempt = LoginAttempt.builder()
				.email(user.getEmail())
				.uuid(user.getUuid())
				.build();
			loginAttemptRepository.save(newLoginAttempt);
			throw new LoginFailException(AuthenticationErrorCode.ERR_LOGIN, newLoginAttempt.getFailCount());
		}

		LoginAttempt loginAttempt = loginAttemptData.get();

		log.info("[LOGIN_ATTEMPT] - {}", loginAttempt.toString());

		// 로그인 횟수 증가
		loginAttempt.increaseAttempt();
		log.info("[LOGIN_ATTEMPT][LOGIN_FAIL_COUNT_INCREASE] - {}", loginAttempt.toString());

		// 최대 로그인 시도 횟수 초과
		if (loginAttempt.isMaxFailCountNumberExceed()) {
			applicationEventPublisher.publishEvent(new AccountLockEvent(user, loginAttempt));
		} else {
			loginAttemptRepository.save(loginAttempt);
		}

		throw new LoginFailException(AuthenticationErrorCode.ERR_LOGIN, loginAttempt.getFailCount());
	}

	/**
	 * 회원가입 요청 처리
	 *
	 * @param registerRequest - 회원가입 요청 데이터
	 * @param request         - 회원가입 요청 http 정보
	 * @param locale
	 * @return
	 */
	@Transactional
	public OAuthTokenResponse register(
		RegisterRequest registerRequest, HttpServletRequest request, Locale locale
	) {
		// 1. 인증 데이터 가져오기
		EmailAuth emailAuth = emailAuthorizationRepository.findById(registerRequest.getEmail())
			.orElseThrow(
				() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_SESSION_EXPIRE));

		// 2. 인증 세션 코드 확인
		if (!emailAuth.getSessionCode().equals(registerRequest.getSessionCode())) {
			log.error("EMAIL_AUTH_SESSION_CODE: [{}], REQUEST_SESSION_CODE: [{}]",
				emailAuth.getSessionCode(), registerRequest.getSessionCode()
			);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_AUTHENTICATION);
		}

		// 3. 프로필 이미지 정보확인
		if (registerRequest.getProfile() != null) {
			// file limit check
			long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;
			if (registerRequest.getProfile().getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
				throw new UserAuthenticationServiceException(
					AuthenticationErrorCode.ERR_REGISTER_PROFILE_IMAGE_MAX_SIZE_EXCEEDED);
			}
			// file type check
			String fileExtension = Files.getFileExtension(
				requireNonNull(registerRequest.getProfile().getOriginalFilename()));
			if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
				throw new UserAuthenticationServiceException(
					AuthenticationErrorCode.ERR_REGISTER_PROFILE_IMAGE_NOT_SUPPORT);
			}
		}

		// 4. 휴대전화번호 확인

		// 4-1. 국제번호만 들어온경우 무시
		if (registerRequest.getMobile() != null && registerRequest.getMobile().matches("^\\+\\d+-$")) {
			log.info(
				"[MOBILE NUMBER SET WITHOUT TAILING NUMBER. ONLY INTERNATIONAL NUMBER] - [{}] convert to empty String",
				registerRequest.getMobile()
			);
			registerRequest.setMobile("");
		}

		if (StringUtils.hasText(registerRequest.getMobile())) {
			if (!registerRequest.getMobile().matches("^\\+\\d+-\\d{10,}$")) {
				log.error("휴대전화번호 형식 불일치(^\\+\\d+-\\d{10,}$) : [{}]", registerRequest.getMobile());
				throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
			}
		}

		// 3. 계정서버에 계정 등록
		log.info("Send User Service Account Register: [{}]", registerRequest.toString());

		ApiResponse<UserInfoResponse> registerApiResponse = userRestService
			.accountRegisterRequest(registerRequest);

		// 4. 계정 등록 오류 발생
		if (registerApiResponse.getCode() != 200) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER);
		}

		// 5. 계정 정보 조회
		UserInfoResponse userInfoResponse = registerApiResponse.getData();
		User user = userRepository.findByEmail(userInfoResponse.getEmail())
			.orElseThrow(() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER));

		OAuthTokenResponse oAuthTokenResponse = getLoginResponse(user, request);

		// 6. 가입 축하 메일 전송
		sendRegisterSuccessMail(user, locale);

		// 7. 만약 비회원 초대를 통한 회원가입인 경우
		if (registerRequest.hasInviteSession()) {
			try {
				log.info(
					"[REGISTRATION][WORKSPACE_INVITE_USER][START] - sessionCode:[{}] , locale:[{}]",
					registerRequest.getSessionCode(), locale.getLanguage()
				);
				workspaceRestService.acceptInviteSession(registerRequest.getInviteSession(), locale.getLanguage());
				log.info(
					"[REGISTRATION][WORKSPACE_INVITE_USER][END] - sessionCode:[{}] , locale:[{}]",
					registerRequest.getSessionCode(), locale.getLanguage()
				);
			} catch (FeignException e) {
				log.error(e.getMessage(), e);
			}
		}

		return oAuthTokenResponse;
	}

	/**
	 * 가입 환영 메일 전송
	 * @param user - 가입 계정 정보
	 * @param locale - 가입 계정의 현재 언어 정보
	 */
	private void sendRegisterSuccessMail(User user, Locale locale) {
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

		EmailMessage registrationSuccessMail = new EmailMessage();
		registrationSuccessMail.setSubject(mailTitle);
		registrationSuccessMail.setTo(user.getEmail());
		registrationSuccessMail.setMessage(message);

		log.info(
			"[SEND_REGISTRATION_SUCCESS_MAIL] - title: {} , to: {}",
			registrationSuccessMail.getSubject(),
			registrationSuccessMail.getTo()
		);

		emailService.sendEmail(registrationSuccessMail);
	}

	/**
	 * 로그아웃 요청 처리
	 *
	 *
	 * @param request
	 * @param response
	 * @param logoutRequest - 로그아웃 요청 정보
	 * @return
	 */
	@Transactional
	public LogoutResponse logout(
		HttpServletRequest request, HttpServletResponse response, LogoutRequest logoutRequest
	) {
		User user = userRepository.findByUuid(logoutRequest.getUuid())
			.orElseThrow(
				() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGOUT_USER_NOT_FOUND));

		BlockToken blockToken = getBlockedToken(logoutRequest.getAccessToken(), user,
			BlockReason.LOGOUT
		);

		blockTokenRepository.save(blockToken);

		if (request.getCookies() != null) {
			// disable remember-me cookie
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(REMEMBER_ME_COOKIE)) {
					log.info(
						"[LOG_OUT][BEFORE][REMEMBER_ME_COOKIE]: {} =>[{}] , {}, {}, {}", cookie.getName(),
						cookie.getValue(),
						cookie.getDomain(), cookie.getMaxAge(), cookie.getPath()
					);
					cookie.setMaxAge(0);
					log.info(
						"[LOG_OUT][AFTER][REMEMBER_ME_COOKIE]: {} =>[{}] , {}, {}, {}", cookie.getName(),
						cookie.getValue(),
						cookie.getDomain(), cookie.getMaxAge(), cookie.getPath()
					);
					response.addCookie(cookie);
				}
			}
		}
		return new LogoutResponse(true, user.getEmail());
	}

	/**
	 * 사용자 인증 토큰 사용 제한 처리
	 *
	 * @param authorizationToken - 사용자 인증 토큰
	 * @param user               - 사용자 계정 정보
	 * @param reason             - 제한 사유
	 * @return
	 */
	private BlockToken getBlockedToken(String authorizationToken, User user, BlockReason reason) {
		JwtPayload jwtPayload = jwtTokenProvider.getJwtPayload(authorizationToken);
		Claims claims = jwtTokenProvider.getClaims(authorizationToken);
		BlockToken blockToken = new BlockToken();
		blockToken.setTokenId(jwtPayload.getJwtId());
		blockToken.setBlockReason(reason);
		blockToken.setBlockedBy(user.getName());
		blockToken.setBlockExpire(claims.getExpiration().getTime());
		blockToken.setTokenPayload(jwtPayload);
		blockToken.setBlockStartDate(LocalDateTime.now());
		return blockToken;
	}

	/**
	 * 이메일 인증 요청 처리
	 *
	 * @param emailAuthRequest - 이메일 인증 요청 정보
	 * @param locale
	 * @return
	 */
	public EmailAuthenticationResponse emailAuthorization(
		EmailAuthRequest emailAuthRequest, Locale locale
	) {
		// 탈퇴 회원의 이메일 정보인지 확인
		checkSecessionUserEmail(emailAuthRequest.getEmail());

		// 이미 가입된 사용자가 있는지 확인
		emailDuplicateCheck(emailAuthRequest);

		// 기존 인증 요청이 있는지 확인
		authorizationRequestDuplicateCheck(emailAuthRequest);

		// 인증 이메일 전송
		sendAuthenticationEmail(emailAuthRequest, locale);

		return new EmailAuthenticationResponse(true);
	}

	private void checkSecessionUserEmail(String email) {
		boolean isSecessionUserEmail = secessionUserRepository.existsByEmail(email);
		if (isSecessionUserEmail) {
			log.info("[EMAIL_AUTHENTICATION] [SECESSION_USER] : [{}]", email);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_SECESSION_USER_EMAIL);
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

	/**
	 * 회원가입 인증 이메일 중복 여부 확인
	 *
	 * @param emailAuthRequest - 이메일 인증 요청 정보
	 */
	private void emailDuplicateCheck(EmailAuthRequest emailAuthRequest) {
		boolean hasDuplicatedEmail = userRepository.existsByEmail(emailAuthRequest.getEmail());

		// Check email is Duplicate
		if (hasDuplicatedEmail) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_DUPLICATED_EMAIL);
		}
	}

	/**
	 * 로그인 응답 요청 생성
	 *
	 * @param user    - 계정 정보
	 * @param request - 로그인 요청 http 정보
	 * @return
	 */
	@Transactional
	public OAuthTokenResponse getLoginResponse(User user, HttpServletRequest request) {
		verifyDevice(user, request);
		return getOAuthTokenResponse(user, request);
	}

	/**
	 * 사용자 인증 토큰 생성
	 *
	 * @param user    - 사용자 계정 정보
	 * @param request - 요청 http 정보
	 * @return
	 */
	@Transactional
	public OAuthTokenResponse getOAuthTokenResponse(User user, HttpServletRequest request) {
		ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);
		log.info("CLIENT INFO: [{}]", clientGeoIPInfo);
		String accessTokenJwtId = UUID.randomUUID().toString();
		String refreshTokenJwtId = UUID.randomUUID().toString();
		String accessToken = jwtTokenProvider.createAccessToken(user, accessTokenJwtId, clientGeoIPInfo);
		String refreshToken = jwtTokenProvider.createRefreshToken(
			user, refreshTokenJwtId, accessTokenJwtId, clientGeoIPInfo);

		OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
		oAuthTokenResponse.setAccessToken(accessToken);
		oAuthTokenResponse.setRefreshToken(refreshToken);
		oAuthTokenResponse.setExpireIn(jwtTokenProvider.getAccessTokenExpire());
		oAuthTokenResponse.setPasswordInitialized(user.isAccountPasswordInitialized());

		// if user access by device then save device access log
		deviceAuthenticationService.saveUserDeviceAccessLog(user, request);
		return oAuthTokenResponse;
	}

	/**
	 * 이메일 인증 코드 확인 요청 처리
	 *
	 * @param code  - 인증 코드
	 * @param email - 인증 이메일
	 * @return
	 */
	public EmailVerificationResponse emailVerificationCodeCheck(String code, String email) {
		EmailAuth emailAuth = emailAuthorizationRepository.findById(email)
			.orElseThrow(
				() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_AUTHENTICATION));
		if (!emailAuth.getCode().equals(code)) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REGISTER_AUTHENTICATION);
		}
		return new EmailVerificationResponse(true, emailAuth.getSessionCode());
	}

	/**
	 * 접속 기기 검사
	 *
	 * @param user    - 사용자 계정 정보
	 * @param request - 요청 http 정보
	 */
	public void verifyDevice(User user, HttpServletRequest request) {
		ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);

		UserAccessLog deviceMetadata = UserAccessLog.builder()
			.user(user)
			.deviceDetails(clientGeoIPInfo.getDeviceDetails())
			.ip(clientGeoIPInfo.getIp())
			.location(clientGeoIPInfo.getLocation())
			.country(clientGeoIPInfo.getCountry())
			.countryCode(clientGeoIPInfo.getCountryCode())
			.lastLoggedIn(LocalDateTime.now())
			.userAgent(clientGeoIPInfo.getUserAgent())
			.build();
		userAccessLogRepository.save(deviceMetadata);
	}

	/**
	 * 로그인 OTP QRCode 생성
	 *
	 * @param qrGenerateRequest - QRCode 생성 요청
	 * @return - QR 생성 정보
	 */
	@Transactional
	public OTPQRGenerateResponse otpQRCodeGenerate(OTPQRGenerateRequest qrGenerateRequest) {
		try {
			boolean isAuthenticatedUser = userRepository.existsByUuid(qrGenerateRequest.getUserId());

			if (!isAuthenticatedUser) {
				throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_OTP_QR_CODE_CREATE);
			}

			boolean hasOtpRegistered = userOTPRepository.existsByEmail(qrGenerateRequest.getEmail());

			if (hasOtpRegistered) {
				userOTPRepository.deleteAllByEmail(qrGenerateRequest.getEmail());
			}

			final GoogleAuthenticatorKey authenticatorKey = googleAuthenticator
				.createCredentials(qrGenerateRequest.getEmail());
			String otpAuthUrl = GoogleAuthenticatorQRGenerator
				.getOtpAuthTotpURL("VIRNECT", qrGenerateRequest.getEmail(), authenticatorKey);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			log.info("OTP AUTH URL : [{}]", otpAuthUrl);
			BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 500, 500);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
			String qrCode = Base64.getEncoder().encodeToString(outputStream.toByteArray());
			return new OTPQRGenerateResponse(otpAuthUrl, qrCode);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_OTP_QR_CODE_CREATE);
		}
	}

	/**
	 * QR TOTP 로그인 요청 처리
	 *
	 * @param otpLoginRequest - totp login request
	 * @param request         - client request
	 * @return
	 */
	@Transactional
	public OAuthTokenResponse otpLoginHandler(
		OTPLoginRequest otpLoginRequest, HttpServletRequest request
	) {
		try {
			boolean isVerified = googleAuthenticator
				.authorizeUser(otpLoginRequest.getEmail(), otpLoginRequest.getCode());
			log.info("OTP Code Validation Result: {}", isVerified);
			if (!isVerified) {
				throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGIN);
			}

			User user = userRepository.findByEmail(otpLoginRequest.getEmail())
				.orElseThrow(() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGIN));

			return getLoginResponse(user, request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGIN);
		}
	}

	/**
	 * AccessToken 재발급
	 *
	 * @param tokenRefreshRequest - AccessToken renewal request(accessToken, refreshToken)
	 * @param request
	 * @return
	 */
	@Transactional
	public RefreshTokenResponse generateNewAccessToken(
		TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request
	) {
		try {
			log.info("ACCESS TOKEN: {}", tokenRefreshRequest.getAccessToken());
			log.info("REFRESH TOKEN: {}", tokenRefreshRequest.getRefreshToken());
			String encodedPayload = tokenRefreshRequest.getAccessToken().split("\\.")[1];
			log.info("ACCESS TOKEN PAYLOAD BASE64 DECODE: {}", TextCodec.BASE64URL.decodeToString(encodedPayload));
			JwtPayload accessToken = objectMapper.readValue(
				TextCodec.BASE64URL.decodeToString(encodedPayload), JwtPayload.class);
			JwtPayload refreshToken = jwtTokenProvider.getJwtPayload(tokenRefreshRequest.getRefreshToken());

			RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
			long currentTimeMillis = new Date().getTime() / 1000;
			if (accessToken.getExp() - currentTimeMillis >= 600) {
				refreshTokenResponse.setAccessToken(tokenRefreshRequest.getAccessToken());
				refreshTokenResponse.setRefreshToken(tokenRefreshRequest.getRefreshToken());
				refreshTokenResponse.setExpireIn(accessToken.getExp() - currentTimeMillis);
				refreshTokenResponse.setRefreshed(false);
				return refreshTokenResponse;
			}

			if (!jwtTokenProvider.isValidToken(tokenRefreshRequest.getRefreshToken()) || !accessToken.getJwtId()
				.equals(refreshToken.getAccessTokenJwtId())) {
				throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
			}

			User user = userRepository.findByUuid(refreshToken.getUuid())
				.orElseThrow(
					() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION));

			ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);

			refreshTokenResponse.setAccessToken(
				jwtTokenProvider.createAccessToken(user, accessToken.getJwtId(), clientGeoIPInfo));
			refreshTokenResponse.setRefreshToken(tokenRefreshRequest.getRefreshToken());
			refreshTokenResponse.setExpireIn(jwtTokenProvider.getAccessTokenExpire());
			refreshTokenResponse.setRefreshed(true);

			return refreshTokenResponse;

		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException | IllegalArgumentException e) {
			log.error(e.getMessage(), e);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REFRESH_ACCESS_TOKEN);
		}
	}

	/**
	 * 계정 무결성 검사
	 *
	 * @param user
	 */
	private void accountValidation(User user) {
		// 휴면 계정인 경우
		if (!user.isAccountNonExpired()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_ACCOUNT_EXPIRED);
		}

		// 계정이 잠긴 경우
		if (!user.isAccountNonLocked()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_ACCOUNT_LOCK);
		}

		// 계정 비밀번호가 만료된 경우
		if (!user.isCredentialsNonExpired()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_ACCOUNT_CREDENTIALS_EXPIRED);
		}

		// 계정이 비활성화 된 경우
		if (!user.isEnabled()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_ACCOUNT_NOT_ENABLE);
		}
	}
}
