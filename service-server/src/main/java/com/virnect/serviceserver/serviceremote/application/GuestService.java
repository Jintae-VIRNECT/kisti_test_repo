package com.virnect.serviceserver.serviceremote.application;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.account.AccountRestService;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.response.guest.GuestInfoResponse;
import com.virnect.data.dto.response.room.RoomInfoResponse;
import com.virnect.data.dto.rest.GuestAccountInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

	private final RoomService roomService;
	private final AccountRestService accountRestService;
	private final RoomRepository roomRepository;

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
			if (guestAccount.getCode() == 5000)	{
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
		Room openRoom = roomRepository.findRoomByGuest(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(openRoom)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		RoomInfoResponse roomInfoResponse = roomService.makeRoomInfo(workspaceId, openRoom);
		return new ApiResponse<>(roomInfoResponse);
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

	private String parseXForwardedHeader(String header)	{
		return header.split(" *,*")[0];
	}

}
