package com.virnect.uaa.domain.auth.account.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SessionLogoutRequest {
	@NotBlank(message = "사용자 식별자 정보가 반드시 있어야합니다.")
	@ApiModelProperty(value = "사용자 식별자", notes = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String uuid;
}
