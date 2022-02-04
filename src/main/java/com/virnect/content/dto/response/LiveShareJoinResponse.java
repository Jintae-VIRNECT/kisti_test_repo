package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.domain.Role;

@Getter
@RequiredArgsConstructor
public class LiveShareJoinResponse {
	private final Long roomId;
	private final String userUUID;
	private final Role role;
	private final LocalDateTime joinedDate;

	@Builder
	public LiveShareJoinResponse(LiveShareUser liveShareUser) {
		this.roomId = liveShareUser.getRoomId();
		this.userUUID = liveShareUser.getUserUUID();
		this.role = liveShareUser.getUserRole();
		this.joinedDate = liveShareUser.getCreatedDate();
	}
}