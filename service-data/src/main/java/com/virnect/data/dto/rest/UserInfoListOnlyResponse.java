package com.virnect.data.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoListOnlyResponse {
	private List<UserInfoResponse> userInfoList;

	@Override
	public String toString() {
		return "UserInfoListOnlyResponse{" +
			"userInfoList=" + userInfoList +
			'}';
	}
}
