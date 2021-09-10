package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class GuestMemberRegistrationRequest {
	@NotBlank(message = "워크스페이스 마스터 사용자 식별정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "마스터 사용자 식별자 정보", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String masterUserUUID;
	@NotBlank(message = "워크스페이스 식별정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "워크스페이스 식별자 정보", position = 1, example = "asdjalsd")
	private String workspaceUUID;

	@Override
	public String toString() {
		return "GuestMemberRegistrationRequest{" +
				"masterUserUUID='" + masterUserUUID + '\'' +
				", workspaceUUID='" + workspaceUUID + '\'' +
				'}';
	}
}
