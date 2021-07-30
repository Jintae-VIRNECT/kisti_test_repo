package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ApiModel
public class UserDeleteResponse {
	@ApiModelProperty(value = "삭제된 사용자의 식별자 정보", example = "asdaskjdhaksdasd")
	private final String userUUID;
	@ApiModelProperty(value = "삭제된 날짜정보", position = 1, example = "2020-01-20T14:05:30")
	private final LocalDateTime deletedDate;

	@Override
	public String toString() {
		return "UserDeleteResponse{" +
			"userUUID='" + userUUID + '\'' +
			", deletedDate=" + deletedDate +
			'}';
	}
}
