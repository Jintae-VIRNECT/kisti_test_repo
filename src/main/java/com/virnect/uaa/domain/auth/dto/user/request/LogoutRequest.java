package com.virnect.uaa.domain.auth.dto.user.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.16
 */
@Getter
@Setter
@ApiModel
public class LogoutRequest {
	@NotBlank(message = "사용자 식별자 정보가 반드시 있어야합니다.")
	@ApiModelProperty(value = "사용자 식별자", notes = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String uuid;
	@NotBlank(message = "API 토큰 정보가 반드시 있어야합니다.")
	@ApiModelProperty(value = "로그인 결과로 받은 api 인증 토큰", position = 1, notes = "api 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTTUlDIE1hc3RlciIsInV1aWQiOiI0OThiMTgzOWRjMjllZDdiYjJlZTkwYWQ2OTg1YzYwOCIsImVtYWlsIjoic21pYzEiLCJuYW1lIjoiU01JQyBNYXN0ZXIiLCJqd3RJZCI6IjIzZTMzNWZlLWVjYTktNDU0Ni1iMDQzLWNjYTUzYzIyYzk3ZSIsImlhdCI6MTU4NTYxODE3MSwiZXhwIjoxNTg1NjE4MjcxLCJpc3MiOiJ0ZXN0LWFkbWluIn0.hLUHZLZkJ91AXxcBy_M2_nx32RoQb6qybNxcOtXFkiY")
	private String accessToken;
}
