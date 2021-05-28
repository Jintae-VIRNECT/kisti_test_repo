package com.virnect.data.redis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@RedisHash("nonmemberAuth")
public class NonmemberAuth {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "auth_code")
	private String authCode;

	@Override
	public String toString() {
		return "NonmemberAuth{" +
			"id='" + id + '\'' +
			", authCode='" + authCode + '\'' +
			'}';
	}
}
