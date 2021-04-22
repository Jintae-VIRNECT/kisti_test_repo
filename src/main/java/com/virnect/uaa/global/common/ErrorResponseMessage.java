package com.virnect.uaa.global.common;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;

@Getter
@NoArgsConstructor
@ApiModel
public class ErrorResponseMessage {
	@ApiModelProperty(value = "에러 응답 코드")
	private int code;
	@ApiModelProperty(value = "서비스명")
	private String service;
	@ApiModelProperty(value = "에러 응답 메시지")
	private String message;
	@ApiModelProperty(value = "에러 응답 데이터")
	private Map<String, Object> data;

	public ErrorResponseMessage(final String service, final String message, final int code) {
		this.code = code;
		this.message = message;
		this.service = service;
		data = new HashMap<>();
	}

	public static ErrorResponseMessage parseError(UserAccountErrorCode error) {
		return new ErrorResponseMessage("user", error.getMessage(), error.getCode());
	}

	public static ErrorResponseMessage parseError(AuthenticationErrorCode error) {
		return new ErrorResponseMessage("auth", error.getMessage(), error.getCode());
	}
}
