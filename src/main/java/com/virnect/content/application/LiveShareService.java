package com.virnect.content.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.dao.contentliveshare.LiveShareRoomRepository;
import com.virnect.content.dao.contentliveshare.LiveShareUserRepository;
import com.virnect.content.domain.LiveShareRoom;
import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.domain.Role;
import com.virnect.content.dto.response.LiveShareResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveShareService {
	private final LiveShareUserRepository liveShareUserRepository;
	private final LiveShareRoomRepository liveShareRoomRepository;
	private final ContentRepository contentRepository;

	@Transactional
	public LiveShareResponse joinLiveShareRoom(
		String contentUUID, String workspaceUUID, String userUUID
	) {
		contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		LiveShareRoom activeRoom = liveShareRoomRepository.getActiveRoomByContentUUID(contentUUID);

		if (activeRoom != null) {
			LiveShareUser liveShareUser = LiveShareUser.liveShareUserBuilder()
				.roomId(activeRoom.getId())
				.userUUID(userUUID)
				.userRole(Role.FOLLOWER)
				.build();
			liveShareUserRepository.save(liveShareUser);
			return new LiveShareResponse(liveShareUser);
		}
		LiveShareRoom liveShareRoom = LiveShareRoom.liveShareRoomBuilder()
			.contentUUID(contentUUID)
			.workspaceUUID(workspaceUUID)
			.build();
		liveShareRoomRepository.save(liveShareRoom);

		LiveShareUser liveShareUser = LiveShareUser.liveShareUserBuilder()
			.roomId(liveShareRoom.getId())
			.userUUID(userUUID)
			.userRole(Role.LEADER)
			.build();
		liveShareUserRepository.save(liveShareUser);
		return new LiveShareResponse(liveShareUser);

	}
}
