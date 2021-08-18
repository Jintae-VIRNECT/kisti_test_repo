package com.virnect.uaa.domain.auth.account.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class GuestAccountInfoResponse {
	private String accessToken;
	private String refreshToken;
	private String scope;
	private long expireIn;
	private String tokenType;
	private SeatUserStat seatUserStat;
}
