package com.virnect.data.dto.rest;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GuestAccountInfoResponse {

	private String accessToken;
	private String refreshToken;
	private String scope;
	private long expireIn;
	private String tokenType;
	private SeatUserStat seatUserStat;

}
