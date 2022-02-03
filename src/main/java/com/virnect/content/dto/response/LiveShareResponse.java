package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.domain.Role;

@Getter
@RequiredArgsConstructor
public class LiveShareResponse {
	private final Long roomId;
	private final String userUUID;
	private final Role role;
	private final LocalDateTime createdDate;
	@Builder
	public LiveShareResponse(LiveShareUser liveShareUser) {
		this.roomId = liveShareUser.getRoomId();
		this.userUUID = liveShareUser.getUserUUID();
		this.role = liveShareUser.getUserRole();
		this.createdDate = liveShareUser.getCreatedDate();
	}
}