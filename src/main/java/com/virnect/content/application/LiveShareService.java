package com.virnect.content.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.workspace.WorkspaceRestService;
import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.dao.contentliveshare.LiveShareRoomRepository;
import com.virnect.content.dao.contentliveshare.LiveShareUserRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.LiveShareRoom;
import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.domain.Role;
import com.virnect.content.dto.response.LiveShareJoinResponse;
import com.virnect.content.dto.rest.LiveShareUserUpdatePushRequest;
import com.virnect.content.dto.rest.WorkspaceUserResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.infra.message.MessageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveShareService {
	private final LiveShareUserRepository liveShareUserRepository;
	private final LiveShareRoomRepository liveShareRoomRepository;
	private final ContentRepository contentRepository;
	private final MessageService messageService;
	private final WorkspaceRestService workspaceRestService;

	@Transactional
	public LiveShareJoinResponse joinLiveShareRoom(
		String contentUUID, String userUUID
	) {
		Content content = contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
		String workspaceUUID = content.getWorkspaceUUID();

		validateWorkspaceUser(workspaceUUID, userUUID);

		LiveShareRoom activeRoom = liveShareRoomRepository.getActiveRoomByContentUUID(contentUUID);

		if (activeRoom != null) {
			LiveShareUser newLiveShareUser = LiveShareUser.liveShareUserBuilder()
				.roomId(activeRoom.getId())
				.userUUID(userUUID)
				.userRole(Role.FOLLOWER)
				.build();
			liveShareUserRepository.save(newLiveShareUser);

			sendActiveUserUpdateMessage(contentUUID, activeRoom.getId());

			return new LiveShareJoinResponse(newLiveShareUser);
		}
		LiveShareRoom newLiveShareRoom = LiveShareRoom.liveShareRoomBuilder()
			.contentUUID(contentUUID)
			.workspaceUUID(workspaceUUID)
			.build();
		liveShareRoomRepository.save(newLiveShareRoom);

		LiveShareUser newLiveShareUser = LiveShareUser.liveShareUserBuilder()
			.roomId(newLiveShareRoom.getId())
			.userUUID(userUUID)
			.userRole(Role.LEADER)
			.build();
		liveShareUserRepository.save(newLiveShareUser);

		sendActiveUserUpdateMessage(contentUUID, newLiveShareRoom.getId());
		return new LiveShareJoinResponse(newLiveShareUser);

	}

	private void validateWorkspaceUser(String workspaceUUID, String userUUID) {
		ApiResponse<WorkspaceUserResponse> apiResponse = workspaceRestService.getMemberInfo(workspaceUUID, userUUID);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || !StringUtils.hasText(
			apiResponse.getData().getUuid())) {
			log.error(
				"[REQ - WORKSPACE SERVER][GET WORKSPACE USER INFO] request user uuid : {}, request workspace uuid : {}, response code : {}, response message : {}",
				userUUID, workspaceUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
	}

	private void sendActiveUserUpdateMessage(String contentUUID, Long roomId) {
		List<LiveShareUserUpdatePushRequest> pushRequest = liveShareUserRepository.getActiveUserListByRoomId(
			roomId);
		String exchangeName = "amp.topic";
		String routingKey = String.format("push.contents.%s.room.%s", contentUUID, roomId);
		messageService.convertAndSend(exchangeName, routingKey, pushRequest);
		log.info(
			"[JOIN_LIVE_SHARE_ROOM] ACTIVE USER UPDATE MESSAGE SEND ! EXCHANGE : {}, ROUTING : {}, MESSAGE : {} ",
			exchangeName, routingKey, pushRequest.toString()
		);
	}
}
