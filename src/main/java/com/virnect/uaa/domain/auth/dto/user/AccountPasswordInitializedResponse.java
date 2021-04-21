package com.virnect.uaa.domain.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiModel
@Getter
@RequiredArgsConstructor
public class AccountPasswordInitializedResponse {
	@ApiModelProperty(value = "비밀번호 초기화 세션 코드", example = "asdansdak12312b3123")
	private final String sessionCode;
	@ApiModelProperty(value = "비밀번호 초기화 대상 사용자 아이디", example = "testUser1")
	private final String email;
}
