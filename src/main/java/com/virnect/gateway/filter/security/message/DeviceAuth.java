package com.virnect.gateway.filter.security.message;

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
	private String appVersion;
	private String secretKey;
	private LocalDateTime createdDate;
	@TimeToLive
	private Long expiredAt;

	@Builder
	public DeviceAuth(
		String deviceAuthKey, String deviceId, String deviceType,
		String appName, String appVersion, String secretKey, Long expiredAt
	) {
		this.deviceAuthKey = deviceAuthKey;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.appName = appName;
		this.appVersion = appVersion;
		this.secretKey = secretKey;
		this.expiredAt = expiredAt;
		this.createdDate = LocalDateTime.now();
	}

}
