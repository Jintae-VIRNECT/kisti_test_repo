package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiModel
@Getter
@RequiredArgsConstructor
public class UserInfoListOnlyResponse {
	@ApiModelProperty(value = "사용자 정보 리스트")
	private final List<UserInfoResponse> userInfoList;
}
