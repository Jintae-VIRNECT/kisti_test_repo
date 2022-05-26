package com.virnect.content.global.security;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.global.filter.JwtPayload;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private final ObjectMapper objectMapper;

	@Value("${security.jwt-config.secret}")
	private String secret;

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			return false;
		}
	}

	private String getJwtSecret() {
		return Base64.getEncoder().encodeToString(secret.getBytes());
	}

	public String getTokenByRequest(HttpServletRequest request) {
		String authorizationToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(authorizationToken) && authorizationToken.startsWith(BEARER_PREFIX)) {
			int tokenSize = authorizationToken.length();
			return authorizationToken.substring(7, tokenSize);
		}
		return null;
	}

	public String getTokenByStompHeader(SimpMessageHeaderAccessor accessor) {
		String authorizationToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(authorizationToken) && authorizationToken.startsWith(BEARER_PREFIX)) {
			int tokenSize = authorizationToken.length();
			return authorizationToken.substring(7, tokenSize);
		}
		return null;
	}

	public JwtPayload getTokenPayload(String token) throws JsonProcessingException {
		String payload = token.split("\\.")[1];
		String decodedPayload = TextCodec.BASE64URL.decodeToString(payload);
		return objectMapper.readValue(decodedPayload, JwtPayload.class);
	}

	public Claims parseClaims(String token) {
		return Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(token).getBody();

	}
}