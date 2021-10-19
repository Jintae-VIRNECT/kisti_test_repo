package com.virnect.data.dto.response.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.Role;
import com.virnect.data.redis.domain.AccessType;

@Getter
@Setter
@ApiModel
public class RemoteGroupMemberResponse {

	@ApiModelProperty(value = "User Unique Identifier", position = 1, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String uuid;

	@ApiModelProperty(value = "User Nick Name", position = 2, example = "리모트 데모")
	private String nickName;

	@ApiModelProperty(value = "User Profile image URL", position = 3, example = "url")
	private String profile;

	@ApiModelProperty(value = "User email", position = 4, example = "uuid")
	private String email;

	@ApiModelProperty(value = "User type", position = 5, example = "uuid")
	private String userType;

	@ApiModelProperty(value = "User name", position = 6, example = "uuid")
	private String name;

	@ApiModelProperty(value = "User role", position = 7, example = "uuid")
	private Role role;


	@ApiModelProperty(
		value = "Access type (LOGIN : 접속 중, LOGOUT : 로그아웃(웹소켓 끊김), JOIN(협업 진행 중)", position = 6)
	private AccessType accessType = AccessType.LOGOUT;

	@Override
	public String toString() {
		return "RemoteGroupMemberResponse{" +
			"uuid='" + uuid + '\'' +
			", nickName='" + nickName + '\'' +
			", profile='" + profile + '\'' +
			", email='" + email + '\'' +
			", userType='" + userType + '\'' +
			", name='" + name + '\'' +
			", role='" + role + '\'' +
			", accessType=" + accessType +
			'}';
	}
}
