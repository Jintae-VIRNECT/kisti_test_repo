package com.virnect.download.dto.domain;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class DeviceLatestVersionCodeDto {
	private long deviceId;
	private long versionCode;

	@QueryProjection
	public DeviceLatestVersionCodeDto(long deviceId, long versionCode) {
		this.deviceId = deviceId;
		this.versionCode = versionCode;
	}
}
