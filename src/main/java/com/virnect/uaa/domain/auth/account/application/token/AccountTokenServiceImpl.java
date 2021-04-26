package com.virnect.uaa.domain.auth.account.application.token;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountTokenServiceImpl implements AccountTokenService {
	@Override
	public RefreshTokenResponse refreshAccessToken(
		TokenRefreshRequest tokenRefreshRequest,
		HttpServletRequest request
	) {
		return null;
	}
}
