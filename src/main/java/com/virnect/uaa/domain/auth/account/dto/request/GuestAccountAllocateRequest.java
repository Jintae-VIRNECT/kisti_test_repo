package com.virnect.uaa.domain.auth.account.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestAccountAllocateRequest {
	@NotBlank(message = "워크스페이스 식별자 정보는 반드시 있어야 합니다.")
	private String workspaceId;
	@NotBlank(message = "제품 정보는 반드시 있어야 합니다.")
	private String product;

	@Override
	public String
	toString() {
		return "GuestAccountAllocateRequest{" +
			"workspaceId='" + workspaceId + '\'' +
			", product='" + product + '\'' +
			'}';
	}
}
