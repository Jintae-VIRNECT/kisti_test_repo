package com.virnect.data.dto.response.guest;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.dto.response.room.RoomDetailInfoResponse;

@Getter
@Setter
@Builder
@ApiModel
public class GuestInfoResponse {

	private String workspaceId;
	private String accessToken;
	private String refreshToken;
	private int expireIn;
	private RoomDetailInfoResponse roomInfoResponse;

	@Override
	public String toString() {
		return "GuestInfoResponse{" +
			"workspaceId='" + workspaceId + '\'' +
			", accessToken='" + accessToken + '\'' +
			", refreshToken='" + refreshToken + '\'' +
			", expireIn=" + expireIn +
			", roomInfoResponse=" + roomInfoResponse +
			'}';
	}
}
