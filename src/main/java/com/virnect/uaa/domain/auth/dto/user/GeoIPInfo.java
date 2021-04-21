package com.virnect.uaa.domain.auth.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.13
 */

@Getter
@RequiredArgsConstructor
public class GeoIPInfo {
	private final String ip;
	private final String location;
	private final String country;
	private final String countryCode;
}
