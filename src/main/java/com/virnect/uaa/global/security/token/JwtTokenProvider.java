package com.virnect.uaa.global.security.token;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.user.domain.Permission;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserPermission;
import com.virnect.uaa.global.config.token.TokenProperty;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Jwt Generate Class
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final ModelMapper modelMapper;
	private final TokenProperty tokenProperty;

	@Transactional
	public String createAccessToken(User user, String jwtId, ClientGeoIPInfo clientGeoIPInfo) {
		Date now = new Date();
		Date expireDate;
		expireDate = new Date((now.getTime() + tokenProperty.getJwtConfig().getAccessTokenExpire()));
		DateTimeFormatter registerDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		List<String> authorities = new ArrayList<>();

		for (UserPermission userPermission : user.getUserPermissionList()) {
			Permission permission = userPermission.getPermission();
			authorities.add(permission.getPermission());
		}

		log.info("[CREATE_ACCESS_TOKEN][EXP] - IAT: {}, EXP: {}", now.toString(), expireDate.toString());

		return Jwts.builder()
			.setSubject(user.getName())
			.claim("userId", user.getId())
			.claim("uuid", user.getUuid())
			.claim("name", user.getName())
			.claim("email", user.getEmail())
			.claim("registerDate", user.getCreatedDate().format(registerDateTimeFormatter))
			.claim("ip", clientGeoIPInfo.getIp())
			.claim("country", clientGeoIPInfo.getCountry())
			.claim("countryCode", clientGeoIPInfo.getCountryCode())
			.claim("authorities", authorities)
			.claim("jwtId", jwtId)
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.setIssuer(tokenProperty.getJwtConfig().getIssuer())
			.signWith(SignatureAlgorithm.HS256, getJwtSecret())
			.compact();
	}

	@Transactional
	public String createRefreshToken(
		User user, String jwtId, String accessTokenJwtId, ClientGeoIPInfo clientGeoIPInfo
	) {
		Date now = new Date();
		Date expireDate;
		expireDate = new Date((now.getTime() + tokenProperty.getJwtConfig().getRefreshTokenExpire()));
		log.info("[CREATE_REFRESH_TOKEN][EXP] - IAT: {}, EXP: {}", now.toString(), expireDate.toString());

		List<String> authorities = new ArrayList<>();
		for (UserPermission userPermission : user.getUserPermissionList()) {
			Permission permission = userPermission.getPermission();
			authorities.add(permission.getPermission());
		}

		return Jwts.builder()
			.setSubject(user.getName())
			.claim("uuid", user.getUuid())
			.claim("name", user.getName())
			.claim("email", user.getEmail())
			.claim("ip", clientGeoIPInfo.getIp())
			.claim("country", clientGeoIPInfo.getCountry())
			.claim("countryCode", clientGeoIPInfo.getCountryCode())
			.claim("jwtId", jwtId)
			.claim("accessTokenJwtId", accessTokenJwtId)
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.setIssuer(tokenProperty.getJwtConfig().getIssuer())
			.signWith(SignatureAlgorithm.HS256, getJwtSecret())
			.compact();
	}

	/**
	 * Request 의 Authorization Header 필드에서 Bearer 스키마 타입의 JWT 토큰 추출
	 *
	 * @param request - 클라이언트 요청
	 * @return - JWT 토큰 문자열
	 */
	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			int tokenSize = bearerToken.length();
			return bearerToken.substring(7, tokenSize);
		}
		return null;
	}

	public boolean isValidToken(String authorizationToken) {
		try {
			Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(authorizationToken);
			return true;
		} catch (JwtException e) {
			log.info("Invalid JWT Signature " + e);
			log.debug("Exception " + e.getMessage());
			return false;
		}
	}

	public JwtPayload getJwtPayload(String authorizationToken) {
		Claims claims = Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(authorizationToken).getBody();
		return modelMapper.map(claims, JwtPayload.class);
	}

	public Claims getClaims(String authorizationToken) {
		return Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(authorizationToken).getBody();
	}

	public long getAccessTokenExpire() {
		return tokenProperty.getJwtConfig().getAccessTokenExpire();
	}

	private String getJwtSecret() {
		return Base64.getEncoder().encodeToString(tokenProperty.getJwtConfig().getSecret().getBytes());
	}

}
