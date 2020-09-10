package com.virnect.download.global.error;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@NoArgsConstructor
@ApiModel
public class ErrorResponseMessage {
	@ApiModelProperty(value = "에러 응답 코드")
	private int code;
	@ApiModelProperty(value = "에러 응답 서비스명")
	private String service;
	@ApiModelProperty(value = "에러 응답 메시지")
	private String message;
	@ApiModelProperty(value = "에러 응답 데이터")
	private Map<String, Object> data;

	public ErrorResponseMessage(final ErrorCode error) {
		this.code = error.getCode();
		this.service = "download";
		this.message = error.getMessage();
		data = new HashMap<>();
	}
}
