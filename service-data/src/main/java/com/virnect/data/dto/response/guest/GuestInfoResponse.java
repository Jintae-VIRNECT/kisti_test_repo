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
	private String uuid;
	private String name;
	private String nickname;
	private String accessToken;
	private String refreshToken;
	private long expireIn;

	@Override
	public String toString() {
		return "GuestInfoResponse{" +
			"workspaceId='" + workspaceId + '\'' +
			", uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", nickname='" + nickname + '\'' +
			", accessToken='" + accessToken + '\'' +
			", refreshToken='" + refreshToken + '\'' +
			", expireIn=" + expireIn +
			'}';
	}
}
