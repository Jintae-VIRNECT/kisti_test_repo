package com.virnect.uaa.domain.auth.account.application.signin;

import java.time.LocalDateTime;
import java.util.Arrays;
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

import com.virnect.uaa.domain.auth.account.dao.BlockTokenRepository;
import com.virnect.uaa.domain.auth.account.dao.LoginAttemptRepository;
import com.virnect.uaa.domain.auth.account.domain.BlockReason;
import com.virnect.uaa.domain.auth.account.domain.BlockToken;
import com.virnect.uaa.domain.auth.account.domain.LoginAttempt;
import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.error.exception.LoginFailException;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.domain.auth.account.event.account.AccountLockEvent;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.global.common.ClientUserAgentInformationParser;
import com.virnect.uaa.global.security.token.JwtPayload;
import com.virnect.uaa.global.security.token.JwtTokenProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSignInServiceImpl implements AccountSignInService {
	private static final String REMEMBER_ME_COOKIE = "remember-me";
	private final UserRepository userRepository;
	private final LoginAttemptRepository loginAttemptRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final UserAccessLogRepository userAccessLogRepository;
	private final BlockTokenRepository blockTokenRepository;
	private final ClientUserAgentInformationParser clientUserAgentInformationParser;

	@Transactional
	@Override
	public OAuthTokenResponse login(
		LoginRequest loginRequest,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		User user;

		// login authentication processing
		if (loginRequest.isRememberMe()) {
			user = rememberMeLoginAuthentication(request);
		} else {
			user = userIdAndPasswordLoginAuthentication(loginRequest);
		}

		// account status check
		accountStatusValidate(user);

		// remove login attempt history
		removeLoginAttemptHistory(user.getEmail());

		// save account access log
		ClientGeoIPInfo clientGeoIPInfo = saveUserAccessLogInformation(user, request);

		return getOauthLoginResponse(user, clientGeoIPInfo);
	}

	@Transactional
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

	public OAuthTokenResponse getOauthLoginResponse(
		User user, ClientGeoIPInfo clientGeoIPInfo
	) {
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

	public Cookie getRememberMeCookie(HttpServletRequest request) {
		return Arrays.stream(request.getCookies())
			.filter(c -> c.getName().equals(REMEMBER_ME_COOKIE))
			.findFirst()
			.orElseThrow(
				() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER)
			);
	}

	public User rememberMeLoginAuthentication(HttpServletRequest request) {
		Cookie rememberMeCookie = getRememberMeCookie(request);
		if (!jwtTokenProvider.isValidToken(rememberMeCookie.getValue())) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
		}
		JwtPayload userInfo = jwtTokenProvider.getJwtPayload(rememberMeCookie.getValue());
		log.info("RememberMeLoginAuthentication - JwtPayload: [{}]", userInfo);
		return findUserByUserUUID(userInfo.getUuid(), AuthenticationErrorCode.ERR_LOGIN);
	}

	public User userIdAndPasswordLoginAuthentication(LoginRequest loginRequest) {
		log.info("UserIDAndPasswordLoginAuthentication - LoginRequest: [{}]", loginRequest);
		User loginUser = findUserByUserEmail(loginRequest.getEmail(), AuthenticationErrorCode.ERR_LOGIN);

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

	public ClientGeoIPInfo saveUserAccessLogInformation(User user, HttpServletRequest request) {
		ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);
		UserAccessLog deviceMetadata = UserAccessLog.ofUserAndClientGeoIPInfo(user, clientGeoIPInfo);
		userAccessLogRepository.save(deviceMetadata);
		return clientGeoIPInfo;
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
}
