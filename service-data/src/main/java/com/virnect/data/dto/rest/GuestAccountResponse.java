package com.virnect.data.dto.rest;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GuestAccountResponse {

	private String accessToken;
	private String refreshToken;
	private String scope;
	private String tokenType;
	private int expireIn;
	private GuestUserStat guestUserStat;

}
