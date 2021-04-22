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
 * @since 2020.03.17
 */
@Getter
@RequiredArgsConstructor
@ApiModel
public class EmailAuthenticationResponse {
	@ApiModelProperty(value = "이메일 인증 요청 처리 결과", example = "true")
	private final boolean result;
}
