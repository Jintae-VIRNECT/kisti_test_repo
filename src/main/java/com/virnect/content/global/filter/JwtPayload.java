package com.virnect.content.global.filter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-10-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class JwtPayload {
	private String sub;
	private long userId;
	private String uuid;
	private String name;
	private String email;
	private String ip;
	private String country;
	private String countryCode;
	private String registerDate;
	private List<String> authorities;
	private String jwtId;
	private String accessTokenJwtId;
	private long iat;
	private long exp;
	private String iss;

	@Override
	public String toString() {
		return "JwtPayload{" +
			"sub='" + sub + '\'' +
			", userId=" + userId +
			", uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", email='" + email + '\'' +
			", ip='" + ip + '\'' +
			", country='" + country + '\'' +
			", countryCode='" + countryCode + '\'' +
			", registerDate='" + registerDate + '\'' +
			", authorities=" + authorities +
			", jwtId='" + jwtId + '\'' +
			", accessTokenJwtId='" + accessTokenJwtId + '\'' +
			", iat=" + iat +
			", exp=" + exp +
			", iss='" + iss + '\'' +
			'}';
	}
}
