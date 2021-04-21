package com.virnect.uaa.global.security.token;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Jwt Pay load Class
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
			", iat=" + iat +
			", exp=" + exp +
			", iss='" + iss + '\'' +
			'}';
	}
}
