package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@ApiModel
@RequiredArgsConstructor
public class UserSecessionResponse {
	@ApiModelProperty(value = "탈퇴 회원의 이메일 정보", example = "test@test.com")
	private final String email;
	@ApiModelProperty(value = "탈퇴 회원의 이름", position = 1, example = "고길동")
	private final String name;
	@ApiModelProperty(value = "탈퇴 처리 일자", position = 2, example = "2020-05-12T10:01:44.0")
	private final LocalDateTime secessionDate;

	@Override
	public String toString() {
		return "UserSecessionResponse{" +
			"email='" + email + '\'' +
			", name='" + name + '\'' +
			", secessionDate=" + secessionDate +
			'}';
	}
}
