package com.virnect.uaa.domain.auth.security.middleware;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class DeviceAuthenticationEncryptedResponse {
	@ApiModelProperty(value = "암호화된 메시지 전문")
	private String data;

	@Override
	public String toString() {
		return "EncodingRequest{" +
			"data='" + data + '\'' +
			'}';
	}
}
