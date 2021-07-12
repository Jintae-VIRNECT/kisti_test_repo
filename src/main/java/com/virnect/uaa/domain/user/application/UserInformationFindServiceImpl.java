package com.virnect.uaa.domain.user.application;

import java.util.Locale;

import org.springframework.stereotype.Service;

import com.virnect.uaa.domain.user.dto.request.EmailFindRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeRequest;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindAuthCodeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindCodeCheckResponse;

@Service
public class UserInformationFindServiceImpl
	implements UserInformationFindService {
	@Override
	public UserEmailFindResponse findUserEmail(
		EmailFindRequest emailFindRequest
	) {
		return null;
	}

	@Override
	public UserPasswordFindAuthCodeResponse sendPasswordResetEmail(
		UserPasswordFindAuthCodeRequest passwordAuthCodeRequest,
		Locale locale
	) {
		return null;
	}

	@Override
	public UserPasswordFindCodeCheckResponse verifyPasswordResetCode(
		UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest
	) {
		return null;
	}

	@Override
	public UserPasswordChangeResponse renewalPreviousPassword(
		UserPasswordChangeRequest passwordChangeRequest
	) {
		return null;
	}
}
