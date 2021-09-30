package com.virnect.uaa.domain.auth.account.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.13
 */

@Setter
@Getter
public class ClientGeoIPInfo {
	private String ip;
	private String country;
	private String countryCode;
	private String location;
	private String deviceDetails;
	private String userAgent;

	@Override
	public String toString() {
		return "ClientGeoIPInfo{" +
			"ip='" + ip + '\'' +
			", country='" + country + '\'' +
			", countryCode='" + countryCode + '\'' +
			", location='" + location + '\'' +
			", deviceDetails='" + deviceDetails + '\'' +
			", userAgent='" + userAgent + '\'' +
			'}';
	}
}
