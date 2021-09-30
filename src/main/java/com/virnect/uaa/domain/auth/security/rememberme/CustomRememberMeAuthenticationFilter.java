package com.virnect.uaa.domain.auth.security.rememberme;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.filter.GenericFilterBean;

public class CustomRememberMeAuthenticationFilter extends GenericFilterBean implements
	ApplicationEventPublisherAware {

	// ~ Instance fields
	// ================================================================================================

	private final AuthenticationManager authenticationManager;
	private final RememberMeServices rememberMeServices;
	private final CompositeSessionAuthenticationStrategy sessionControlAuthenticationStrategy;
	private ApplicationEventPublisher eventPublisher;
	private AuthenticationSuccessHandler successHandler;
	private AuthenticationFailureHandler failureHandler;

	public CustomRememberMeAuthenticationFilter(
		AuthenticationManager authenticationManager,
		RememberMeServices rememberMeServices,
		CompositeSessionAuthenticationStrategy sessionControlAuthenticationStrategy
	) {
		this.authenticationManager = authenticationManager;
		this.rememberMeServices = rememberMeServices;
		this.sessionControlAuthenticationStrategy = sessionControlAuthenticationStrategy;
	}

	@Override
	public void doFilter(
		ServletRequest req, ServletResponse res, FilterChain chain
	) throws IOException, ServletException {
		// super.doFilter(req, res, chain);
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			Authentication rememberMeAuth = rememberMeServices.autoLogin(
				request,
				response
			);

			if (rememberMeAuth != null) {
				// Attempt authenticaton via AuthenticationManager
				try {
					rememberMeAuth = authenticationManager.authenticate(rememberMeAuth);

					// Session Check Current Authentication User Via Remember Me Cookie
					sessionControlAuthenticationStrategy.onAuthentication(rememberMeAuth, request, response);

					// Store to SecurityContextHolder
					SecurityContextHolder.getContext().setAuthentication(rememberMeAuth);

					onSuccessfulAuthentication(request, response, rememberMeAuth);

					if (logger.isDebugEnabled()) {
						logger.debug("SecurityContextHolder populated with remember-me token: '"
							+ SecurityContextHolder.getContext().getAuthentication()
							+ "'");
					}

					// Fire event
					if (this.eventPublisher != null) {
						eventPublisher
							.publishEvent(new InteractiveAuthenticationSuccessEvent(
								SecurityContextHolder.getContext()
									.getAuthentication(), this.getClass()));
					}

					if (successHandler != null) {
						successHandler.onAuthenticationSuccess(request, response,
							rememberMeAuth
						);

						return;
					}

				} catch (AuthenticationException authenticationException) {

					if (logger.isDebugEnabled()) {
						logger.debug(
							"SecurityContextHolder not populated with remember-me token, as "
								+ "AuthenticationManager rejected Authentication returned by RememberMeServices: '"
								+ rememberMeAuth
								+ "'; invalidating remember-me token",
							authenticationException
						);
					}

					rememberMeServices.loginFail(request, response);

					onUnsuccessfulAuthentication(request, response,
						authenticationException
					);

					return;
				}
			}

			chain.doFilter(request, response);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("SecurityContextHolder not populated with remember-me token, as it already contained: '"
					+ SecurityContextHolder.getContext().getAuthentication() + "'");
			}

			chain.doFilter(request, response);
		}
	}

	protected void onSuccessfulAuthentication(
		HttpServletRequest request, HttpServletResponse response,
		Authentication authResult
	) {
	}

	protected void onUnsuccessfulAuthentication(
		HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed
	) throws IOException, ServletException {
		if (failed instanceof SessionAuthenticationException) {
			logger.error("CustomRememberMeAuthentication - Session Duplication Error");
			logger.error(failed.getMessage());
			this.failureHandler.onAuthenticationFailure(request, response, failed);
		}

	}

	@Override
	public void setApplicationEventPublisher(
		ApplicationEventPublisher applicationEventPublisher
	) {
		this.eventPublisher = eventPublisher;
	}

	public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
		this.successHandler = successHandler;
	}

	public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
		this.failureHandler = failureHandler;
	}
}
