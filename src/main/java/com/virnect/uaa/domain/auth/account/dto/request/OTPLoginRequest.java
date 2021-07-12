package com.virnect.uaa.domain.auth.account.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.15
 */
@Getter
@Setter
@ApiModel
public class OTPLoginRequest {
	@NotNull(message = "인증코드는 반드시 입력도어야 합니다.")
	@ApiModelProperty(value = "OTP QR 코드를 바탕으로 생성된 OTP 코드", example = "855541")
	private int code;
	@NotBlank(message = "이메일 정보는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "OTP 로그인 시도 사용자의 이메일", example = "sky456139@virnect.com")
	private String email;

	@Override
	public String toString() {
		return "OTPLoginRequest{" +
			"code=" + code +
			", email='" + email + '\'' +
			'}';
	}
}
