package com.virnect.data.dto.response.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.redis.domain.AccessType;

@Getter
@Setter
@ApiModel
public class RemoteGroupMemberResponse {

	@ApiModelProperty(value = "User Unique Identifier", position = 1, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String uuid = "";

	@ApiModelProperty(value = "User Nick Name", position = 2, example = "리모트 데모")
	private String nickName = "";

	@ApiModelProperty(value = "User Profile image URL", position = 3, example = "url")
	private String profile = "";

	@ApiModelProperty(
		value = "Access type (LOGIN : 접속 중, LOGOUT : 로그아웃(웹소켓 끊김), JOIN(협업 진행 중)", position = 4)
	private AccessType accessType = AccessType.LOGOUT;

}
