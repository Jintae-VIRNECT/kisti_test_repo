package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class SeatMemberRegistrationRequest {
	@NotBlank(message = "워크스페이스 마스터 사용자 식별정보는 반드시 있어야 합니다.")
	private String masterUserUUID;
	@NotBlank(message = "워크스페이스 식별정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "워크스페이스 식별자 정보", example = "asdjalsd")
	private String workspaceUUID;

	@Override
	public String toString() {
		return "SeatMemberRegistrationRequest{" +
			"masterUserUUID='" + masterUserUUID + '\'' +
			", workspaceUUID='" + workspaceUUID + '\'' +
			'}';
	}
}
