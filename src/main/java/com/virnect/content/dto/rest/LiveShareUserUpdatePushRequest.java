package com.virnect.content.dto.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

import com.virnect.content.domain.Role;

@Getter
public class LiveShareUserUpdatePushRequest {
	private final String nickname;
	private final String email;
	private final String uuid;
	private final Role role;
	private final String joinedDate;

	@QueryProjection

	public LiveShareUserUpdatePushRequest(
		String nickname, String email, String uuid, Role role, LocalDateTime createdDate
	) {
		this.nickname = nickname;
		this.email = email;
		this.uuid = uuid;
		this.role = role;
		this.joinedDate = createdDate.format(DateTimeFormatter.ISO_DATE_TIME);
	}

	@Override
	public String toString() {
		return "LiveShareUserUpdatePushRequest{" +
			"nickname='" + nickname + '\'' +
			", email='" + email + '\'' +
			", uuid='" + uuid + '\'' +
			", role=" + role +
			", joinedDate='" + joinedDate + '\'' +
			'}';
	}
}
