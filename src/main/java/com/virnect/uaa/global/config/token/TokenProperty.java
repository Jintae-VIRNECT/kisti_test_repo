package com.virnect.uaa.global.config.token;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConstructorBinding
@ConfigurationProperties(prefix = "security")
@Validated
public class TokenProperty {
	@NotNull(message = "JWT 토큰 설정 정보가 있어야 합니다.")
	private final JwtTokenProperty jwtConfig;
	@NotBlank(message = "자동 로그인 쿠키의 도메인 또는 아이피 설정 정보가 있어야 합니다.")
	private final String cookieDomain;
	@NotBlank(message = "자동 로그인 쿠키의 요청 경로 정보가 있어야 합니다.")
	private final String cookiePath;
	@NotNull(message = "쿠키의 유효 시간 정보가 있어야 합니다.")
	private final long cookieMaxAgeDay;

	public TokenProperty(JwtTokenProperty jwtConfig, String cookieDomain, String cookiePath, long cookieMaxAgeDay) {
		this.jwtConfig = jwtConfig;
		this.cookieDomain = cookieDomain;
		this.cookiePath = cookiePath;
		this.cookieMaxAgeDay = cookieMaxAgeDay;
	}

	public JwtTokenProperty getJwtConfig() {
		return jwtConfig;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public long getCookieMaxAgeDay() {
		return cookieMaxAgeDay;
	}

	@Override
	public String toString() {
		return "TokenProperty{" +
			"jwtTokenProperty=" + jwtConfig +
			", cookieDomain='" + cookieDomain + '\'' +
			", cookiePath='" + cookiePath + '\'' +
			", cookieMaxAgeDay=" + cookieMaxAgeDay +
			'}';
	}
}
