package com.virnect.data.dto.rest;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GuestAccountInfoResponse {

	private String uuid;
	private String name;
	private String nickname;
	private String accessToken;
	private String refreshToken;
	private String scope;
	private long expireIn;
	private String tokenType;
	private SeatUserStat seatUserStat;

	@Override
	public String toString() {
		return "GuestAccountInfoResponse{" +
			"uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", nickname='" + nickname + '\'' +
			", accessToken='" + accessToken + '\'' +
			", refreshToken='" + refreshToken + '\'' +
			", scope='" + scope + '\'' +
			", expireIn=" + expireIn +
			", tokenType='" + tokenType + '\'' +
			", seatUserStat=" + seatUserStat +
			'}';
	}
}
