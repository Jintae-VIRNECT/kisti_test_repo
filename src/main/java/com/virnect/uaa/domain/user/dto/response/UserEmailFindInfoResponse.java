package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserEmailFindInfoResponse {
	@ApiModelProperty(value = "찾은 이메일 정보", example = "ema1**@********")
	private String email;
	@ApiModelProperty(value = "회원가입 일자", example = "2020-04-01")
	private LocalDate signUpDate;
}
