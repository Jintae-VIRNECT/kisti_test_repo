package com.virnect.content.dto.rest;

import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Getter;

import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.domain.Role;

@Getter
public class LiveShareUserUpdatePushRequest {
	private final String nickname;
	private final String email;
	private final String uuid;
	private final Role role;
	private final String joinedDate;

	@Builder
	public LiveShareUserUpdatePushRequest(
		LiveShareUser liveShareUser
	) {
		this.nickname = liveShareUser.getUserNickname();
		this.email = liveShareUser.getUserEmail();
		this.uuid = liveShareUser.getUserUUID();
		this.role = liveShareUser.getUserRole();
		this.joinedDate = liveShareUser.getCreatedDate().format(DateTimeFormatter.ISO_DATE_TIME);
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
