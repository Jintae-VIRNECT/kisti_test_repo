package com.virnect.uaa.global.config.token;

import java.time.Duration;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public class JwtTokenProperty {
	@NotBlank(message = "JWT 서명키 정보는 반드시 있어야 합니다.")
	private final String secret;
	@NotBlank(message = "JWT 서명자 정보는 반드시 있어야 합니다.")
	private final String issuer;
	@NotNull(message = "ACCESS TOKEN 만료 시간은 반드시 있어야 합니다.")
	@Max(value = 3900, message = "ACCESS TOKEN 만료 시간은 최대 한 시간 입니다.(Seconds)")
	private final long accessTokenExpire;
	@NotNull(message = "REFRESH TOKEN 만료 시간은 반드시 있어야 합니다.")
	@Max(value = 2678400, message = "REFRESH TOKEN 만료 시간은 최대 30일 입니다.(Seconds)")
	private final long refreshTokenExpire;

	public JwtTokenProperty(String secret, String issuer, long accessTokenExpire, long refreshTokenExpire) {
		this.secret = secret;
		this.issuer = issuer;
		this.accessTokenExpire = accessTokenExpire;
		this.refreshTokenExpire = refreshTokenExpire;
	}

	public String getSecret() {
		return secret;
	}

	public String getIssuer() {
		return issuer;
	}

	public long getAccessTokenExpire() {
		return Duration.ofSeconds(accessTokenExpire).toMillis();
	}

	public long getRefreshTokenExpire() {
		return Duration.ofSeconds(refreshTokenExpire).toMillis();
	}

	@Override
	public String toString() {
		return "JwtTokenProperty{" +
			"secret='" + secret + '\'' +
			", issuer='" + issuer + '\'' +
			", accessTokenExpire=" + accessTokenExpire +
			", refreshTokenExpire=" + refreshTokenExpire +
			'}';
	}
}
