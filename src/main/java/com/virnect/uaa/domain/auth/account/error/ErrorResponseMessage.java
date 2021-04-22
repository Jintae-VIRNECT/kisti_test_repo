package com.virnect.uaa.domain.auth.account.error;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Error Response Message
 */
@Getter
@Setter
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

	public ErrorResponseMessage(final AuthenticationErrorCode error) {
		this.code = error.getCode();
		this.message = error.getMessage();
		this.service = "auth";
		data = new HashMap<>();
	}
}
