package com.virnect.uaa.domain.auth.account.application.signup;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.domain.User;

@Slf4j
@Profile(value = "onpremise")
@Service
public class OnPremiseSignUpSuccessHandler implements SignUpSuccessHandler {
	@Override
	public void signUpSuccess(
		User user, HttpServletRequest request, Locale locale
	) {
		log.info("DefaultSuccessHandler - SignUp Success");
	}
}
