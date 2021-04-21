package com.virnect.uaa.domain.auth.dto.device.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class DeviceAuthenticationRequest {
	@NotBlank
	@ApiModelProperty(value = "패키지명", example = "com.virnect.remote.mobile")
	private String packageName;
	@NotBlank
	@ApiModelProperty(value = "암호화 된 앱 인증 데이터 정보", position = 1)
	private String data;

	@Override
	public String toString() {
		return "DeviceAuthenticationRequest{" +
			"packageName='" + packageName + '\'' +
			", data='" + data + '\'' +
			'}';
	}
}
