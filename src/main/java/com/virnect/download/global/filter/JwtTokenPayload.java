package com.virnect.download.global.filter;

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
public class JwtTokenPayload {
	private String iss;
	private int exp;
	private int iat;
	private String jwtId;
	private List<String> authorities;
	private String countryCode;
	private String country;
	private String ip;
	private String registerDate;
	private String email;
	private String name;
	private String uuid;
	private int userId;
	private String sub;

	@Override
	public String toString() {
		return "JwtTokenPayload{" +
			"iss='" + iss + '\'' +
			", exp=" + exp +
			", iat=" + iat +
			", jwtId='" + jwtId + '\'' +
			", authorities=" + authorities +
			", countryCode='" + countryCode + '\'' +
			", country='" + country + '\'' +
			", ip='" + ip + '\'' +
			", registerDate='" + registerDate + '\'' +
			", email='" + email + '\'' +
			", name='" + name + '\'' +
			", uuid='" + uuid + '\'' +
			", userId=" + userId +
			", sub='" + sub + '\'' +
			'}';
	}
}
