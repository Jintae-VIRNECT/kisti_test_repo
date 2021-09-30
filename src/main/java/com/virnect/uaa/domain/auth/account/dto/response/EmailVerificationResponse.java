package com.virnect.uaa.domain.auth.account.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.26
 */
@Getter
@RequiredArgsConstructor
@ApiModel
public class EmailVerificationResponse {
	@ApiModelProperty(value = "인증 코드의 인증 확인 유무", example = "true")
	private final boolean hasAuthenticate;
	@ApiModelProperty(value = "인증 세션 코드", notes = "회원가입 관련한 세션에 대한 세션 코드입니다.", position = 2, example = "f74f09cd2ff143d2b49d27eaa81bfe09")
	private final String sessionCode;

	@Override
	public String toString() {
		return "EmailVerificationResult{" +
			"hasAuthenticate=" + hasAuthenticate +
			", sessionCode='" + sessionCode + '\'' +
			'}';
	}
}
