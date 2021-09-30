package com.virnect.uaa.domain.auth.device.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("DeviceAuth")
public class DeviceAuth {
	@Id
	private String deviceAuthKey;
	private String deviceId;
	private String deviceType;
	private String appName;
	private long appVersionCode;
	private String appVersionName;
	private String secretKey;
	private LocalDateTime createdDate;
	@TimeToLive
	private Long expiredAt;

	@Builder
	public DeviceAuth(
		String deviceAuthKey, String deviceId, String deviceType, String appName,
		String appVersionName, long appVersionCode, String secretKey, Long expiredAt
	) {
		this.deviceAuthKey = deviceAuthKey;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.appName = appName;
		this.appVersionName = appVersionName;
		this.appVersionCode = appVersionCode;
		this.secretKey = secretKey;
		this.expiredAt = expiredAt;
		this.createdDate = LocalDateTime.now();
	}

}
