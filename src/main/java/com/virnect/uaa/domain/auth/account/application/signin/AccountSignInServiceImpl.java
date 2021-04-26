package com.virnect.uaa.domain.auth.account.application.signin;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.LoginAttemptRepository;
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
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
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

	@Override
	public LogoutResponse logout(
		LogoutRequest logoutRequest,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		return null;
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
		return findUserByUserUUID(userInfo.getUuid());
	}

	public User userIdAndPasswordLoginAuthentication(LoginRequest loginRequest) {
		log.info("UserIDAndPasswordLoginAuthentication - LoginRequest: [{}]", loginRequest);
		User loginUser = findUserByUserEmail(loginRequest.getEmail());

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

	public User findUserByUserUUID(String uuid) {
		return userRepository.findByUuid(uuid)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_LOGIN));
	}

	public User findUserByUserEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_LOGIN));
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
