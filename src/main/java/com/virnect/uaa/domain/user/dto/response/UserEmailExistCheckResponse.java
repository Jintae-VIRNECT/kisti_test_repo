package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiModel
@Getter
@RequiredArgsConstructor
public class UserEmailExistCheckResponse {
	@ApiModelProperty(value = "검색 이메일", example = "test132")
	private final String email;
	@ApiModelProperty(value = "검색 결과(존재 여부)", position = 1, example = "true")
	private final boolean result;
	@ApiModelProperty(value = "검색 일자", position = 2, example = "2020-05-12T10:01:44.0")
	private final LocalDateTime existCheckDate;

	@Override
	public String toString() {
		return "UserEmailExistCheckResponse{" +
			"email='" + email + '\'' +
			", result=" + result +
			", existCheckDate=" + existCheckDate +
			'}';
	}
}
