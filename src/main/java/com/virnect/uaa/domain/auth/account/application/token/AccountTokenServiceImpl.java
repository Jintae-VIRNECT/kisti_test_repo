package com.virnect.uaa.domain.auth.account.application.token;

import static io.jsonwebtoken.impl.TextCodec.*;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;
import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.global.common.ClientUserAgentInformationParser;
import com.virnect.uaa.global.security.token.JwtPayload;
import com.virnect.uaa.global.security.token.JwtProvider;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountTokenServiceImpl implements AccountTokenService {
	private final ObjectMapper objectMapper;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final ClientUserAgentInformationParser clientUserAgentInformationParser;

	@Override
	public RefreshTokenResponse refreshAccessToken(
		TokenRefreshRequest tokenRefreshRequest,
		HttpServletRequest request
	) {
		try {
			log.info("ACCESS TOKEN: {}", tokenRefreshRequest.getAccessToken());
			log.info("REFRESH TOKEN: {}", tokenRefreshRequest.getRefreshToken());
			String encodedPayload = tokenRefreshRequest.getAccessToken().split("\\.")[1];
			log.info("ACCESS TOKEN PAYLOAD BASE64 DECODE: {}", BASE64URL.decodeToString(encodedPayload));
			JwtPayload accessToken = objectMapper.readValue(BASE64URL.decodeToString(encodedPayload), JwtPayload.class);
			JwtPayload refreshToken = jwtProvider.getJwtPayload(tokenRefreshRequest.getRefreshToken());

			RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
			long currentTimeMillis = new Date().getTime() / 1000;
			if (accessToken.getExp() - currentTimeMillis >= 600) {
				refreshTokenResponse.setAccessToken(tokenRefreshRequest.getAccessToken());
				refreshTokenResponse.setRefreshToken(tokenRefreshRequest.getRefreshToken());
				refreshTokenResponse.setExpireIn(accessToken.getExp() - currentTimeMillis);
				refreshTokenResponse.setRefreshed(false);
				return refreshTokenResponse;
			}

			if (!jwtProvider.isValidToken(tokenRefreshRequest.getRefreshToken()) || !accessToken.getJwtId()
				.equals(refreshToken.getAccessTokenJwtId())) {
				throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
			}

			User user = userRepository.findByUuid(refreshToken.getUuid())
				.orElseThrow(
					() -> new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION));

			ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);

			refreshTokenResponse.setAccessToken(
				jwtProvider.createAccessToken(user, accessToken.getJwtId(), clientGeoIPInfo));
			refreshTokenResponse.setRefreshToken(tokenRefreshRequest.getRefreshToken());
			refreshTokenResponse.setExpireIn(jwtProvider.getAccessTokenExpire());
			refreshTokenResponse.setRefreshed(true);

			return refreshTokenResponse;

		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException | IllegalArgumentException e) {
			log.error(e.getMessage(), e);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_API_AUTHENTICATION);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_REFRESH_ACCESS_TOKEN);
		}
	}
}
