package com.virnect.uaa.domain.auth.account.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.17
 */

@Getter
@Setter
@ApiModel
public class RefreshTokenResponse {
	@ApiModelProperty(value = "API 인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTTUlDIE1hc3RlciIsInV1aWQiOiI0OThiMTgzOWRjMjllZDdiYjJlZTkwYWQ2OTg1YzYwOCIsImVtYWlsIjoic21pYzEiLCJuYW1lIjoiU01JQyBNYXN0ZXIiLCJqd3RJZCI6IjIzZTMzNWZlLWVjYTktNDU0Ni1iMDQzLWNjYTUzYzIyYzk3ZSIsImlhdCI6MTU4NTYxODE3MSwiZXhwIjoxNTg1NjE4MjcxLCJpc3MiOiJ0ZXN0LWFkbWluIn0.hLUHZLZkJ91AXxcBy_M2_nx32RoQb6qybNxcOtXFkiY", notes = "인증 토큰은 직접 발급 받아야합니다.")
	private String accessToken;
	@ApiModelProperty(value = "API 인증 갱신 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTTUlDIE1hc3RlciIsInV1aWQiOiI0OThiMTgzOWRjMjllZDdiYjJlZTkwYWQ2OTg1YzYwOCIsImVtYWlsIjoic21pYzEiLCJuYW1lIjoiU01JQyBNYXN0ZXIiLCJqd3RJZCI6IjFmZjBjYTUyLWVlZGYtNDBjNi05OTAzLWU2MGU4MDhhZDJiMSIsImlhdCI6MTU4NTYxODE3MSwiZXhwIjoxNTg1NjE4MjcxLCJpc3MiOiJ0ZXN0LWFkbWluIn0.Uy4vY2RxRiYc88YK8XgNetL6k_DDyk_F28FixYi9NNo", position = 1, notes = "인증 토큰 발급시 같이 발급 받는 값입니다.")
	private String refreshToken;
	@ApiModelProperty(value = "인증 토큰의 권한", example = "read,write", position = 2, notes = "인증 토큰 발급시 같이 발급 받는 값입니다.")
	private String scope = "read,write";
	@ApiModelProperty(value = "액세스 토큰 만료 시간(초)", example = "1000", position = 3, notes = "인증 토큰 발급시 같이 발급 받는 값입니다.")
	private long expireIn;
	@ApiModelProperty(value = "인증 스키마 방식", example = "Bearer", position = 5, notes = "인증 토큰 발급시 같이 발급 받는 값입니다.")
	private String tokenType = "Bearer";
	@ApiModelProperty(value = "신규 토큰 발급 여부", example = "true", position = 6, notes = "신규 인증 토큰값인지에 대한 유무 값입니다.")
	private boolean isRefreshed;

	@Override
	public String toString() {
		return "LoginResponse{" +
			"accessToken='" + accessToken + '\'' +
			", refreshToken='" + refreshToken + '\'' +
			", scope='" + scope + '\'' +
			", expireIn=" + expireIn +
			", tokenType='" + tokenType + '\'' +
			", isRefreshed='" + isRefreshed + '\'' +
			'}';
	}
}
