package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@ApiModel
@RequiredArgsConstructor
public class UserIdentityCheckResponse {
	@ApiModelProperty(value = "비밀번호 변경 대상 계정의 이메일", example = "test1234")
	private final String email;
	@ApiModelProperty(value = "비밀번호 변경 대상 계정의 이메일", position = 1, example = "asdnn12308skas31241kskkskals1")
	private final String uuid;
	@ApiModelProperty(value = "질의응답 확인 날짜", position = 2, example = "2020-01-20T14:05:30")
	private final LocalDateTime identityCheckDate;

	@Override
	public String toString() {
		return "UserIdentityCheckResponse{" +
			"email='" + email + '\'' +
			", uuid='" + uuid + '\'' +
			", identityCheckDate='" + identityCheckDate + '\'' +
			'}';
	}
}
