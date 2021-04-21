package com.virnect.uaa.domain.user.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description Email Authentication Domain Class
 * @since 2020.03.19
 */
@Getter
@RedisHash("EmailAuth")
public class EmailAuth {
	@Id
	private String email;
	private String code;
	private String sessionCode;
	private LocalDateTime createdDate;
	@TimeToLive
	private Long expireDate;

	@Builder
	public EmailAuth(String email, String code, String sessionCode, Long expireDate) {
		this.email = email;
		this.code = code;
		this.sessionCode = sessionCode;
		this.expireDate = expireDate;
		this.createdDate = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "EmailAuth{" +
			"email='" + email + '\'' +
			", code='" + code + '\'' +
			", sessionCode='" + sessionCode + '\'' +
			", createdDate=" + createdDate +
			", expireDate=" + expireDate +
			'}';
	}
}
