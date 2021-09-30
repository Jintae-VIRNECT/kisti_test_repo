package com.virnect.data.redis.domain;

import java.time.Duration;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("accessStatus")
public class AccessStatus {
	@Id
	private String id; // worksapceId_userUUID
	private String email;
	private AccessType accessType;
	@TimeToLive
	private Long expiresIn; // default 23 hour

	public AccessStatus(String id, String email, AccessType accessType) {
		this.id = id;
		this.email = email;
		this.accessType = accessType;
		this.expiresIn = Duration.ofHours(23).getSeconds();
	}

	@Override
	public String toString() {
		return "AccessStatus{" +
			"id='" + id + '\'' +
			", email='" + email + '\'' +
			", accessType=" + accessType +
			", expiresIn=" + expiresIn +
			'}';
	}
}
