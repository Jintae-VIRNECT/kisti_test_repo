package com.virnect.uaa.domain.user.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description Authentication code for user password initialization
 * @since 2020.04.13
 */

@Getter
@Setter
@RedisHash(value = "PasswordInitAuthCode")
public class PasswordInitAuthCode {
	@Id
	private String email;
	private String name;
	private String uuid;
	private String code;
	@TimeToLive
	private Long expiredDate;

	public PasswordInitAuthCode(String email, String name, String uuid, String code, Long expiredDate) {
		this.email = email;
		this.name = name;
		this.uuid = uuid;
		this.code = code;
		this.expiredDate = expiredDate;
	}

	public static PasswordInitAuthCode of(
		User user, String code, long expireSeconds
	) {
		return new PasswordInitAuthCode(user.getEmail(), user.getName(), user.getUuid(), code, expireSeconds);
	}

	@Override
	public String toString() {
		return "PasswordInitAuthCode{" +
			"email='" + email + '\'' +
			", name='" + name + '\'' +
			", uuid='" + uuid + '\'' +
			", code='" + code + '\'' +
			", expiredDate=" + expiredDate +
			'}';
	}
}
