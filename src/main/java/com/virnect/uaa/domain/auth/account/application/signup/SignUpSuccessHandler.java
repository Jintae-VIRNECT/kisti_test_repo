package com.virnect.uaa.domain.auth.account.application.signup;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.virnect.uaa.domain.user.domain.User;

public interface SignUpSuccessHandler {
	void signUpSuccess(User user, HttpServletRequest request, Locale locale);
}
