package com.virnect.serviceserver.serviceremote.application;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.account.AccountRestService;
import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.request.guest.GuestEventRequest;
import com.virnect.data.dto.request.room.JoinRoomRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.guest.GuestInfoResponse;
import com.virnect.data.dto.response.room.RoomInfoResponse;
import com.virnect.data.dto.response.room.RoomResponse;
import com.virnect.data.dto.rest.GuestAccountInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.mediaserver.core.EndReason;
import com.virnect.serviceserver.serviceremote.dao.SessionDataRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

	private static final String TAG = GuestService.class.getSimpleName();
	private static final String REST_PATH = "/remote/room";

	private final RoomService roomService;
	private final AccountRestService accountRestService;
	private final RoomRepository roomRepository;
	private final WorkspaceRestService workspaceRestService;

	private final MemberRepository memberRepository;
	private final SessionDataRepository sessionDataRepository;
	private final ServiceSessionManager serviceSessionManager;

	/*public ApiResponse<GuestInviteUrlResponse> createGuestInviteUrl(String workspaceId, String sessionId) {

		Room room = roomRepository.findRoomByGuest(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		return new ApiResponse<>(GuestInviteUrlResponse.builder()
			.workspaceId(workspaceId)
			.sessionId(sessionId)
			.url(config.remoteServiceProperties.getRemoteServicePublicUrl() + "/remote/invitation/guest/" + workspaceId + "/" + sessionId)
			.build());
	}*/

	public ApiResponse<GuestInfoResponse> getGuestInfo(
		String workspaceId,
		HttpServletRequest request
	) {
		// Guest 계정 정보
		ApiResponse<GuestAccountInfoResponse> guestAccount = accountRestService.getGuestAccountInfo(
			"remote",
			workspaceId,
			request.getHeader("user-agent"),
			extractIpFromRequest(request)
		);
		if (guestAccount.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
			log.info("ACCOUNT SERVER ERROR : " + guestAccount.getCode() + "(" + guestAccount.getMessage() + ")");
			if (guestAccount.getCode() == 5000) {
				return new ApiResponse<>(ErrorCode.ERR_GUEST_USER_NOT_FOUND);
			} else if (guestAccount.getCode() == 5001) {
				return new ApiResponse<>(ErrorCode.ERR_GUEST_USER_NOT_ENOUGH);
			}
		}
		log.info("guest account toString : " + guestAccount.getData().toString());

		GuestInfoResponse guestInfoResponse = GuestInfoResponse.builder()
			.workspaceId(workspaceId)
			.uuid(guestAccount.getData().getUuid())
			.name(guestAccount.getData().getName())
			.nickname(guestAccount.getData().getNickname())
			.accessToken(guestAccount.getData().getAccessToken())
			.refreshToken(guestAccount.getData().getRefreshToken())
			.expireIn(guestAccount.getData().getExpireIn())
			.build();

		return new ApiResponse<>(guestInfoResponse);
	}

	public ApiResponse<RoomInfoResponse> getOpenRoomInfo(
		String workspaceId,
		String sessionId
	) {
		Room openRoom = roomRepository.findOpenRoomByGuest(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(openRoom)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		RoomInfoResponse roomInfoResponse = roomService.makeRoomInfo(workspaceId, openRoom);
		return new ApiResponse<>(roomInfoResponse);
	}

	public ApiResponse<RoomResponse> joinRoomOnlyGuest(
		String workspaceId,
		String sessionId,
		JoinRoomRequest joinRoomRequest
	) {

		String guestMemberUuid = workspaceRestService.getWorkspaceMember(workspaceId, joinRoomRequest.getUuid())
			.getData()
			.getUuid();
		if (StringUtils.isBlank(guestMemberUuid)) {
			return new ApiResponse<>(ErrorCode.ERR_MEMBER_INVALID);
		}

		ApiResponse<Boolean> dataProcess = this.sessionDataRepository.prepareJoinRoomOnlyGuest(
			workspaceId,
			sessionId,
			joinRoomRequest.getUuid()
		);

		if (!dataProcess.getData()) {
			LogMessage.formedInfo(
				TAG,
				"REST API: POST " + REST_PATH + "/",
				"joinRoomOnlyGuest",
				"process data get false",
				dataProcess.getMessage()
			);
			return new ApiResponse<>(dataProcess.getCode(), dataProcess.getMessage());
		}

		// 세션 및 토큰 생성
		JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
		JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

		// 협업방 참여
		ApiResponse<RoomResponse> responseData = this.sessionDataRepository.joinRoom(
			workspaceId,
			sessionId,
			tokenResult.toString(),
			joinRoomRequest
		);
		//responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
		responseData.getData().setCoturn(roomService.setCoturnListResponse(responseData.getData().getSessionType()));
		return responseData;
	}

	private String extractIpFromRequest(HttpServletRequest request) {
		String clientIp;
		String clientXForwardedForIp = request.getHeader("x-forwarded-for");
		if (StringUtils.isBlank(clientXForwardedForIp)) {
			clientIp = parseXForwardedHeader(clientXForwardedForIp);
		} else {
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}

	private String parseXForwardedHeader(String header) {
		return header.split(" *,*")[0];
	}

	public ApiResponse<ResultResponse> guestEvent(GuestEventRequest guestEvent) {
		ResultResponse resultResponse = new ResultResponse();
		switch (guestEvent.getEvent()) {
			case DELETED_ACCOUNT:
				Member guestMember = memberRepository.findGuestMemberByWorkspaceIdAndUuid(
					guestEvent.getWorkspaceId(),
					guestEvent.getUserId()
				).orElse(null);
				if (ObjectUtils.isEmpty(guestMember)) {
					return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
				}
				if (serviceSessionManager.evictParticipant(
					guestMember.getSessionId(), guestMember.getConnectionId(), EndReason.deletedAccount)
				) {
					resultResponse.setResult(true);
					resultResponse.setUserId(guestEvent.getUserId());
				}
				break;
		}
		return new ApiResponse<>(resultResponse);
	}
}
