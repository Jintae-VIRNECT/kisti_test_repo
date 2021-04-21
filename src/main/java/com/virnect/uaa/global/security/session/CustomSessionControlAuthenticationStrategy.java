package com.virnect.uaa.global.security.session;

import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.event.session.SessionFlushEvent;
import com.virnect.security.UserDetailsImpl;

@Slf4j
@RequiredArgsConstructor
public class CustomSessionControlAuthenticationStrategy<S extends Session> implements MessageSourceAware,
	SessionAuthenticationStrategy {
	private final SpringSessionBackedSessionRegistry<S> sessionRegistry;
	private final ApplicationEventPublisher applicationEventPublisher;
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private boolean exceptionIfMaximumExceeded = false;
	private int maximumSessions = 1;

	@Override
	public void onAuthentication(
		Authentication authentication, HttpServletRequest request, HttpServletResponse response
	) throws SessionAuthenticationException {
		log.info("Session ConCurrent Strategy onAuthentication - Start.");
		String sessionFlushRequest = request.getParameter("sessionFlush");
		List<SessionInformation> sessions = this.sessionRegistry.getAllSessions(authentication.getPrincipal(), false);
		int sessionCount = sessions.size();
		int allowedSessions = getMaximumSessionsForThisUser(authentication);

		log.info("CurrentUserSessionCount: [{}]", sessionCount);
		log.info("MaxSessionNumberPerUser: [{}]", allowedSessions);

		if (sessionCount < allowedSessions) {
			// They haven't got too many login sessions running at present
			return;
		}
		if (allowedSessions == -1) {
			// We permit unlimited logins
			return;
		}

		if (sessionCount == allowedSessions) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				// Only permit it though if this request is associated with one of the
				// already registered sessions
				for (SessionInformation si : sessions) {
					if (si.getSessionId().equals(session.getId())) {
						return;
					}
				}
			}
			// If the session is null, a new one will be created by the parent class,
			// exceeding the allowed number
		}

		if (sessionFlushRequest != null && sessionFlushRequest.equals("true")) {
			UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
			sessions.sort(Comparator.comparing(SessionInformation::getLastRequest));
			int maximumSessionsExceededBy = sessions.size() - allowedSessions + 1;
			List<SessionInformation> sessionsToBeExpired = sessions.subList(0, maximumSessionsExceededBy);
			log.info("Destroy Previous session of this account - Start");
			for (SessionInformation session : sessionsToBeExpired) {
				log.info("Destroy Session - [{}]", session.getSessionId());
				session.expireNow();
				sessionRegistry.removeSessionInformation(session.getSessionId());
				applicationEventPublisher.publishEvent(new SessionFlushEvent(userDetails, session, request, response));
			}
			log.info("Destroy Previous session of this account - End");
			return;
		}

		allowableSessionsExceeded(sessions, allowedSessions, this.sessionRegistry);
	}

	protected int getMaximumSessionsForThisUser(Authentication authentication) {
		return this.maximumSessions;
	}

	protected void allowableSessionsExceeded(
		List<SessionInformation> sessions, int allowableSessions,
		SessionRegistry registry
	) throws SessionAuthenticationException {
		if (this.exceptionIfMaximumExceeded || (sessions == null)) {
			// 세션 갯수 초과
			log.error("세션 갯수 초과 또는 세션 없땅!");
			throw new SessionAuthenticationException(
				this.messages.getMessage("ConcurrentSessionControlAuthenticationStrategy.exceededAllowed",
					new Object[] {allowableSessions}, "Maximum sessions of {0} for this principal exceeded"
				));
		}
	}

	public void setExceptionIfMaximumExceeded(boolean exceptionIfMaximumExceeded) {
		this.exceptionIfMaximumExceeded = exceptionIfMaximumExceeded;
	}

	public void setMaximumSessions(int maximumSessions) {
		this.maximumSessions = maximumSessions;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		Assert.notNull(messageSource, "messageSource cannot be null");
		this.messages = new MessageSourceAccessor(messageSource);
	}
}
