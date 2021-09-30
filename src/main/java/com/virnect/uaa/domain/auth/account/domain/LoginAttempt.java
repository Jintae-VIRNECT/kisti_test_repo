package com.virnect.uaa.domain.auth.account.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description User Login Attempt History Redis Object
 * @since 2020.05.08
 */
@Getter
@Setter
@RedisHash("LoginAttempt")
public class LoginAttempt {
	@Id
	private String email;
	private String uuid;
	private Integer failCount;

	@Builder
	LoginAttempt(String email, String uuid) {
		this.email = email;
		this.uuid = uuid;
		this.failCount = 1;
	}

	public void increaseAttempt() {
		this.failCount++;
	}

	public boolean isMaxFailCountNumberExceed() {
		return this.failCount + 1 > 5;
	}

	@Override
	public String toString() {
		return "LoginAttempt{" +
			"email='" + email + '\'' +
			", uuid='" + uuid + '\'' +
			", failCount=" + failCount +
			'}';
	}
}
