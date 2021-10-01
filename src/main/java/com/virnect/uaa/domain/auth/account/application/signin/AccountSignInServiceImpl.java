package com.virnect.uaa.domain.auth.account.application.signin;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.application.UserAccessLogService;
import com.virnect.uaa.domain.auth.account.dao.BlockTokenRepository;
import com.virnect.uaa.domain.auth.account.dao.LoginAttemptRepository;
import com.virnect.uaa.domain.auth.account.dao.UserOTPRepository;
import com.virnect.uaa.domain.auth.account.domain.BlockReason;
import com.virnect.uaa.domain.auth.account.domain.BlockToken;
import com.virnect.uaa.domain.auth.account.domain.LoginAttempt;
import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;
import com.virnect.uaa.domain.auth.account.event.account.AccountLockEvent;
import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.common.exception.LoginFailException;
import com.virnect.uaa.domain.auth.common.exception.UserAuthenticationServiceException;
import com.virnect.uaa.domain.auth.security.token.JwtPayload;
import com.virnect.uaa.domain.auth.security.token.JwtProvider;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.global.common.TotpQRCodeGenerator;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountSignInServiceImpl implements AccountSignInService {
	private static final String REMEMBER_ME_COOKIE = "remember-me";
	private final UserRepository userRepository;
	private final LoginAttemptRepository loginAttemptRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final BlockTokenRepository blockTokenRepository;
	private final UserOTPRepository userOTPRepository;
	private final TotpQRCodeGenerator totpQRCodeGenerator;
	private final UserAccessLogService userAccessLogService;

	@Override
	public OAuthTokenResponse login(
		LoginRequest loginRequest,
		HttpServletRequest request,
		HttpServletResponse response
	) {

		printheader(request);
		User user;

		// login authentication processing
		if (loginRequest.isRememberMe() && request.getCookies() != null) {
			user = rememberMeLoginAuthentication(request);
		} else {
			user = userIdAndPasswordLoginAuthentication(loginRequest);
		}

		// remove login attempt history
		removeLoginAttemptHistory(user.getEmail());

		// save account access log
		ClientGeoIPInfo clientGeoIPInfo = userAccessLogService.saveUserAccessLogInformation(user, request);

		return getOauthLoginResponse(user, clientGeoIPInfo);
	}

	private void printheader(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			log.debug("[header]-> [{}] : [{}]", headerName, request.getHeader(headerName));
		}
	}

	@Override
	public LogoutResponse logout(
		LogoutRequest logoutRequest,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		log.info("Logout - [{}] - ", logoutRequest);
		User user = findUserByUserUUID(logoutRequest.getUuid(), AuthenticationErrorCode.ERR_LOGOUT);
		// Generate And Save BlockToken From Access Token
		generateAndSaveBlockTokenFromAccessToken(logoutRequest.getAccessToken(), user, BlockReason.LOGOUT);
		// remove remember me cookie
		removeRememberMeCookie(request, response);
		return new LogoutResponse(true, user.getEmail());
	}

	@Override
	public OAuthTokenResponse qrCodeLogin(
		OTPLoginRequest otpLoginRequest,
		HttpServletRequest request
	) {
		User loginUser = otpQRCodeLoginAuthentication(otpLoginRequest);

		// remove login attempt history
		removeLoginAttemptHistory(loginUser.getEmail());

		// save account access log
		ClientGeoIPInfo clientGeoIPInfo = userAccessLogService.saveUserAccessLogInformation(loginUser, request);

		return getOauthLoginResponse(loginUser, clientGeoIPInfo);
	}

	@Override
	public OTPQRGenerateResponse loginQrCodeGenerate(
		OTPQRGenerateRequest otpQrCodeGenerateRequest
	) {
		// Login QR code generate request validation
		loginQrCodeGenerateRequestValidation(otpQrCodeGenerateRequest);

		// Generate totp auth url from user email
		String otpAuthUrl = totpQRCodeGenerator.generateOtpAuthUrl(otpQrCodeGenerateRequest.getEmail());

		// Generate QRCode From totp auth url
		String qrCode = totpQRCodeGenerator.generateQRCodeFromOtpAuthUrl(otpAuthUrl);

		return new OTPQRGenerateResponse(otpAuthUrl, qrCode);
	}

	public void loginQrCodeGenerateRequestValidation(OTPQRGenerateRequest otpQrCodeGenerateRequest) {
		boolean isRegisteredUser = userRepository.existsByUuid(otpQrCodeGenerateRequest.getUserId());

		if (!isRegisteredUser) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_OTP_QR_CODE_CREATE);
		}

		boolean hasOtpRegistered = userOTPRepository.existsByEmail(otpQrCodeGenerateRequest.getEmail());

		if (hasOtpRegistered) {
			userOTPRepository.deleteAllByEmail(otpQrCodeGenerateRequest.getEmail());
		}
	}

	public OAuthTokenResponse getOauthLoginResponse(
		User user, ClientGeoIPInfo clientGeoIPInfo
	) {
		String accessTokenJwtId = getUUIDString();
		String refreshTokenJwtId = getUUIDString();
		String accessToken = jwtProvider.createAccessToken(user, accessTokenJwtId, clientGeoIPInfo);
		String refreshToken = jwtProvider.createRefreshToken(
			user, refreshTokenJwtId, accessTokenJwtId, clientGeoIPInfo);
		OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse();
		oAuthTokenResponse.setAccessToken(accessToken);
		oAuthTokenResponse.setRefreshToken(refreshToken);
		oAuthTokenResponse.setExpireIn(jwtProvider.getAccessTokenExpire());
		oAuthTokenResponse.setPasswordInitialized(true);
		return oAuthTokenResponse;
	}

	public void removeRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {
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
	}

	public void generateAndSaveBlockTokenFromAccessToken(
		String authorizationToken, User user, BlockReason reason
	) {
		BlockToken blockToken = getBlockTokenByAccessToken(authorizationToken, user, reason);
		blockTokenRepository.save(blockToken);
	}

	public BlockToken getBlockTokenByAccessToken(String authorizationToken, User user, BlockReason reason) {
		JwtPayload jwtPayload = jwtProvider.getJwtPayload(authorizationToken);
		Claims claims = jwtProvider.getClaims(authorizationToken);
		BlockToken blockToken = new BlockToken();
		blockToken.setTokenId(jwtPayload.getJwtId());
		blockToken.setBlockReason(reason);
		blockToken.setBlockedBy(user.getName());
		blockToken.setBlockExpire(claims.getExpiration().getTime());
		blockToken.setTokenPayload(jwtPayload);
		blockToken.setBlockStartDate(LocalDateTime.now());
		return blockToken;
	}

	public Cookie getRememberMeCookie(HttpServletRequest request) {
		return Arrays.stream(request.getCookies())
			.filter(c -> c.getName().equals(REMEMBER_ME_COOKIE))
			.findFirst()
			.orElseThrow(
				() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER)
			);
	}

	public User otpQRCodeLoginAuthentication(OTPLoginRequest otpLoginRequest) {
		log.info("otpQRCodeLoginAuthentication - otpLoginRequest: [{}]", otpLoginRequest);

		boolean isVerified = totpQRCodeGenerator.totpLoginAuthentication(
			otpLoginRequest.getEmail(), otpLoginRequest.getCode()
		);

		if (!isVerified) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_LOGIN);
		}

		User user = findUserByUserEmail(otpLoginRequest.getEmail(), AuthenticationErrorCode.ERR_LOGIN);

		// account status check
		accountStatusValidate(user);
		return user;
	}

	public User rememberMeLoginAuthentication(HttpServletRequest request) {
		Cookie rememberMeCookie = getRememberMeCookie(request);
		if (!jwtProvider.isValidToken(rememberMeCookie.getValue())) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
		}
		JwtPayload userInfo = jwtProvider.getJwtPayload(rememberMeCookie.getValue());
		log.info("RememberMeLoginAuthentication - JwtPayload: [{}]", userInfo);
		User user = findUserByUserUUID(userInfo.getUuid(), AuthenticationErrorCode.ERR_LOGIN);
		// account status check
		accountStatusValidate(user);
		return user;
	}

	public User userIdAndPasswordLoginAuthentication(LoginRequest loginRequest) {
		log.info("UserIDAndPasswordLoginAuthentication - LoginRequest: [{}]", loginRequest);
		User loginUser = findUserByUserEmail(loginRequest.getEmail(), AuthenticationErrorCode.ERR_LOGIN);

		// account status check
		accountStatusValidate(loginUser);

		if (!passwordEncoder.matches(loginRequest.getPassword(), loginUser.getPassword())) {
			log.info("userIdAndPasswordLoginAuthentication - Password not matched.");
			userLoginPasswordNotMatchedProcessor(loginUser);
		}

		return loginUser;
	}

	public void userLoginPasswordNotMatchedProcessor(User loginUser) {
		Optional<LoginAttempt> loginAttemptData = loginAttemptRepository.findById(loginUser.getEmail());
		// 만약 로그인 시도 데이터가 없다면
		if (!loginAttemptData.isPresent()) {
			LoginAttempt newLoginAttempt = LoginAttempt.builder()
				.email(loginUser.getEmail())
				.uuid(loginUser.getUuid())
				.build();
			loginAttemptRepository.save(newLoginAttempt);
			throw new LoginFailException(AuthenticationErrorCode.ERR_LOGIN, newLoginAttempt.getFailCount());
		}

		LoginAttempt loginAttempt = loginAttemptData.get();

		log.info("[LOGIN_ATTEMPT] - {}", loginAttempt);

		// 로그인 횟수 증가
		loginAttempt.increaseAttempt();
		log.info("[LOGIN_ATTEMPT][LOGIN_FAIL_COUNT_INCREASE] - {}", loginAttempt);

		// 최대 로그인 시도 횟수 초과
		if (loginAttempt.isMaxFailCountNumberExceed()) {
			applicationEventPublisher.publishEvent(new AccountLockEvent(loginUser, loginAttempt));
		} else {
			loginAttemptRepository.save(loginAttempt);
		}

		throw new LoginFailException(AuthenticationErrorCode.ERR_LOGIN, loginAttempt.getFailCount());
	}

	public void removeLoginAttemptHistory(String userEmail) {
		loginAttemptRepository.deleteById(userEmail);
	}

	public User findUserByUserUUID(String uuid, AuthenticationErrorCode errorCode) {
		return userRepository.findByUuid(uuid)
			.orElseThrow(() -> new UserAuthenticationServiceException(errorCode));
	}

	public User findUserByUserEmail(String email, AuthenticationErrorCode errorCode) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserAuthenticationServiceException(errorCode));
	}

	public void accountStatusValidate(User user) {
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

	public String getUUIDString() {
		return UUID.randomUUID().toString();
	}
}
