package com.virnect.data.redis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("accessStatus")
public class AccessStatus {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "access_type")
	private AccessType accessType;

	@Builder
	public AccessStatus(String id, AccessType accessType) {
		this.id = id;
		this.accessType = accessType;
	}

	@Override
	public String toString() {
		return "AccessStatus{" +
			"id='" + id + '\'' +
			", accessType=" + accessType +
			'}';
	}
}
