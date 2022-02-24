package com.virnect.content.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.user.UserRestService;
import com.virnect.content.application.workspace.WorkspaceRestService;
import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.dao.contentliveshare.LiveShareRoomRepository;
import com.virnect.content.dao.contentliveshare.LiveShareUserRepository;
import com.virnect.content.domain.ActiveOrInactive;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.LiveShareRoom;
import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.domain.Role;
import com.virnect.content.dto.request.LiveShareRoomLeaveRequest;
import com.virnect.content.dto.response.LiveShareJoinResponse;
import com.virnect.content.dto.response.LiveShareLeaveResponse;
import com.virnect.content.dto.response.LiveShareUserRoleUpdateResponse;
import com.virnect.content.dto.rest.LiveShareUserUpdatePushRequest;
import com.virnect.content.dto.rest.UserInfoResponse;
import com.virnect.content.dto.rest.WorkspaceUserResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.util.CurrentUserUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveShareService {
	private static final String EXCHANGE_NAME = "amq.topic";
	private static final int MAX_ROOM_USER_AMOUNT = 8;

	private final LiveShareUserRepository liveShareUserRepository;
	private final LiveShareRoomRepository liveShareRoomRepository;
	private final ContentRepository contentRepository;
	private final RabbitTemplate rabbitTemplate;
	private final WorkspaceRestService workspaceRestService;
	private final UserRestService userRestService;

	@Transactional
	public LiveShareJoinResponse joinLiveShareRoom(
		String contentUUID
	) {
		String userUUID = CurrentUserUtils.getUserUUID();

		Content content = contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
		String workspaceUUID = content.getWorkspaceUUID();

		validateWorkspaceUser(workspaceUUID, userUUID);

		UserInfoResponse userInfo = getUserInfo(userUUID);

		LiveShareRoom activeRoom = liveShareRoomRepository.getActiveRoomByContentUUID(contentUUID);

		if (activeRoom != null) {
			List<LiveShareUser> liveShareUserList = liveShareUserRepository.getActiveUserListByRoomId(
				activeRoom.getId());
			if (liveShareUserList.size() == MAX_ROOM_USER_AMOUNT) {
				throw new ContentServiceException(ErrorCode.ERR_CONTENT_LIVE_SHARE_JOIN_MAX_USER);
			}

			for (LiveShareUser liveShareUser : liveShareUserList) {
				if (liveShareUser.getUserUUID().equals(userUUID)) {
					return new LiveShareJoinResponse(liveShareUser);
				}
			}

			LiveShareUser newLiveShareUser = LiveShareUser.liveShareUserBuilder()
				.roomId(activeRoom.getId())
				.userUUID(userUUID)
				.userNickname(userInfo.getNickname())
				.userEmail(userInfo.getEmail())
				.userRole(Role.FOLLOWER)
				.build();
			liveShareUserRepository.save(newLiveShareUser);

			publishActiveUserUpdateMessage(contentUUID, activeRoom.getId());

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
			.userNickname(userInfo.getNickname())
			.userEmail(userInfo.getEmail())
			.userRole(Role.LEADER)
			.build();
		liveShareUserRepository.save(newLiveShareUser);

		publishActiveUserUpdateMessage(contentUUID, newLiveShareRoom.getId());
		return new LiveShareJoinResponse(newLiveShareUser);

	}

	private UserInfoResponse getUserInfo(String userUUID) {
		ApiResponse<UserInfoResponse> apiResponse = userRestService.getUserInfoByUserUUID(userUUID);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || StringUtils.isEmpty(
			apiResponse.getData().getUuid())) {
			log.error(
				"[REQ - USER SERVER][GET USER INFO] request user uuid : {}, response code : {}, response message : {}",
				userUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
		return apiResponse.getData();
	}

	private void validateWorkspaceUser(String workspaceUUID, String userUUID) {
		ApiResponse<WorkspaceUserResponse> apiResponse = workspaceRestService.getMemberInfo(workspaceUUID, userUUID);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || StringUtils.isEmpty(
			apiResponse.getData().getUuid())) {
			log.error(
				"[REQ - WORKSPACE SERVER][GET WORKSPACE USER INFO] request user uuid : {}, request workspace uuid : {}, response code : {}, response message : {}",
				userUUID, workspaceUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
	}

	@Transactional(readOnly = true)
	public void publishActiveUserUpdateMessage(String contentUUID, Long roomId) {
		List<LiveShareUser> liveShareUserList = liveShareUserRepository.getActiveUserListByRoomId(
			roomId);
		List<LiveShareUserUpdatePushRequest> pushRequest = liveShareUserList.stream()
			.map(liveShareUser -> LiveShareUserUpdatePushRequest.builder().liveShareUser(liveShareUser).build())
			.collect(Collectors.toList());

		String routingKey = String.format("push.contents.%s.rooms.%s", contentUUID, roomId);
		publishTopicMessage(routingKey, pushRequest);
	}

	public void publishContentWriteMessage(String contentUUID, String roomId, String message) {
		String routingKey = String.format("api.contents.%s.rooms.%s", contentUUID, roomId);
		publishTopicMessage(routingKey, message);
	}

	private void publishTopicMessage(String routingKey, Object message) {
		rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, message);
		log.info(
			"[MESSAGE_BROKER][CONVERT_AND_SEND] ACTIVE USER UPDATE MESSAGE SEND ! EXCHANGE : {}, ROUTING : {}",
			EXCHANGE_NAME,
			routingKey
		);
	}

	@Transactional
	public LiveShareLeaveResponse leaveLiveShareRoom(
		String contentUUID, Long roomId,
		LiveShareRoomLeaveRequest liveShareRoomLeaveRequest
	) {
		String userUUID = CurrentUserUtils.getUserUUID();
		LiveShareRoom room = liveShareRoomRepository.getActiveRoomById(roomId)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_LIVE_SHARE_ROOM_NOT_FOUND));

		List<LiveShareUser> userList = liveShareUserRepository.getActiveUserListByRoomId(
			roomId);
		LiveShareUser leaveUser = userList.stream()
			.filter(liveShareUser -> liveShareUser.getUserUUID().equals(userUUID))
			.findAny()
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_LIVE_SHARE_USER_NOT_FOUND));

		if (userList.size() != 1) {
			//두번째로 들어온 사용자에게 리더 양도
			if (Role.LEADER.equals(leaveUser.getUserRole())) {
				Optional.ofNullable(userList.get(1)).ifPresent(secondJoinedUser -> {
					secondJoinedUser.setUserRole(Role.LEADER);
					liveShareUserRepository.save(secondJoinedUser);
				});
			}
			liveShareUserRepository.delete(leaveUser);
			publishActiveUserUpdateMessage(contentUUID, roomId);
			return new LiveShareLeaveResponse(true, LocalDateTime.now());
		}

		if (StringUtils.isEmpty(liveShareRoomLeaveRequest.getData())) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		liveShareUserRepository.delete(leaveUser);

		room.setData(liveShareRoomLeaveRequest.getData());
		room.setStatus(ActiveOrInactive.INACTIVE);
		liveShareRoomRepository.save(room);

		return new LiveShareLeaveResponse(true, LocalDateTime.now());
	}

	@Transactional
	public LiveShareUserRoleUpdateResponse updateLiveShareUserRole(
		String contentUUID, String updateUserUUD, Long roomId
	) {
		String userUUID = CurrentUserUtils.getUserUUID();

		LiveShareRoom room = liveShareRoomRepository.getActiveRoomById(roomId)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_LIVE_SHARE_ROOM_NOT_FOUND));
		LiveShareUser currentUser = liveShareUserRepository.getActiveUserByRoomIdAndUserUUID(
			room.getId(), userUUID).orElseThrow(() -> new ContentServiceException(
			ErrorCode.ERR_CONTENT_LIVE_SHARE_USER_NOT_FOUND));
		LiveShareUser updateUser = liveShareUserRepository.getActiveUserByRoomIdAndUserUUID(
			room.getId(), updateUserUUD).orElseThrow(() -> new ContentServiceException(
			ErrorCode.ERR_CONTENT_LIVE_SHARE_USER_NOT_FOUND));

		if (!Role.LEADER.equals(currentUser.getUserRole())) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_LIVE_SHARE_USER_UPDATE);
		}

		currentUser.setUserRole(Role.FOLLOWER);
		liveShareUserRepository.save(currentUser);

		updateUser.setUserRole(Role.LEADER);
		liveShareUserRepository.save(updateUser);

		publishActiveUserUpdateMessage(contentUUID, room.getId());

		return new LiveShareUserRoleUpdateResponse(true, LocalDateTime.now());

	}
}
