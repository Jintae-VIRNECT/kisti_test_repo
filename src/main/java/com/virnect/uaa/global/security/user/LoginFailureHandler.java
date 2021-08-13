package com.virnect.uaa.global.security.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.LoginAttemptRepository;
import com.virnect.uaa.domain.auth.account.domain.LoginAttempt;
import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.event.account.AccountLockEvent;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.global.common.ErrorResponseMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private final static String LOGIN_FAIL_COUNT_NAME = "failCount";
	private final static String LOGIN_USERNAME_PARAMETER = "email";
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final LoginAttemptRepository loginAttemptRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception
	) throws IOException {
		ErrorResponseMessage errorResponseMessage = null;

		String userEmail = request.getParameter(LOGIN_USERNAME_PARAMETER);

		log.error("LOGIN Fail - Cause:: {}", exception.getClass().getSimpleName());
		log.error("LOGIN Fail - Login Try ID: [{}]", userEmail);

		if (exception instanceof SessionAuthenticationException) {
			log.error("LOGIN Fail - 해당 계정으로 활동 중인 세션 정보 확인.");
			errorResponseMessage = ErrorResponseMessage.parseError(AuthenticationErrorCode.ERR_LOGIN_SESSION_DUPLICATE);
		} else if (exception instanceof BadCredentialsException && StringUtils.hasText(userEmail)) {
			log.error("LOGIN Fail - 계정이 존재하나 비밀번호가 틀림.");
			errorResponseMessage = ErrorResponseMessage.parseError(AuthenticationErrorCode.ERR_LOGIN);
			int loginFailCount = loginAttemptCheck(userEmail);
			if (loginFailCount > 0) {
				errorResponseMessage.getData().put(LOGIN_FAIL_COUNT_NAME, loginFailCount);
			}
		} else if (exception instanceof UsernameNotFoundException) {
			log.error("LOGIN Fail - 계정이 존재하지 않음");
			errorResponseMessage = ErrorResponseMessage.parseError(AuthenticationErrorCode.ERR_LOGIN);
		} else if (exception instanceof LockedException) {
			log.error("LOGIN Fail - 잠긴 계정: {}", userEmail);
			errorResponseMessage = ErrorResponseMessage.parseError(AuthenticationErrorCode.ERR_ACCOUNT_LOCK);
		}

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter loginFailResponseWriter = response.getWriter();
		loginFailResponseWriter.write(objectMapper.writeValueAsString(errorResponseMessage));
		loginFailResponseWriter.flush();
	}

	protected int loginAttemptCheck(String email) {
		Optional<User> userInfo = userRepository.findByEmail(email);

		if (!userInfo.isPresent()) {
			return -1;
		}

		// 로그인 실패이력 정보 조회
		Optional<LoginAttempt> loginAttemptData = loginAttemptRepository.findById(email);

		// 만약 로그인 시도 데이터가 없다면
		if (!loginAttemptData.isPresent()) {
			LoginAttempt newLoginAttempt = LoginAttempt.builder()
				.email(userInfo.get().getEmail())
				.uuid(userInfo.get().getUuid())
				.build();
			loginAttemptRepository.save(newLoginAttempt);
			return 1;
		}

		LoginAttempt loginAttempt = loginAttemptData.get();

		log.info("[LOGIN_ATTEMPT] - {}", loginAttempt.toString());

		// 로그인 횟수 증가
		loginAttempt.increaseAttempt();
		log.info("[LOGIN_ATTEMPT][LOGIN_FAIL_COUNT_INCREASE] - {}", loginAttempt.toString());

		// 최대 로그인 시도 횟수 초과
		if (loginAttempt.isMaxFailCountNumberExceed()) {
			applicationEventPublisher.publishEvent(new AccountLockEvent(userInfo.get(), loginAttempt));
			return loginAttempt.getFailCount();
		} else {
			loginAttemptRepository.save(loginAttempt);
		}

		return loginAttempt.getFailCount();
	}
}
