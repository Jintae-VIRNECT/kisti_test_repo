package com.virnect.uaa.domain.auth.account.application.token;

import javax.servlet.http.HttpServletRequest;

import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;

public interface AccountTokenService {
	RefreshTokenResponse refreshAccessToken(
		TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request
	);
}
