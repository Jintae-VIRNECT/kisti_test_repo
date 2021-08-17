package com.virnect.license.dto.license.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLicenseInfoRetrieveRequest {
	@NotBlank
	@ApiModelProperty(value = "워크스페이스 식별자 정보")
	private String workspaceId;
	@ApiModelProperty(value = "사용자 식별자 배열")
	@NotNull
	private List<String> userIds = new ArrayList<>();
	@ApiModelProperty(value = "라이선스 제품명")
	private String product;

	@Override
	public String toString() {
		return "UserLicenseInfoRetrieveRequest{" +
			"workspaceId='" + workspaceId + '\'' +
			", userIds=" + userIds +
			'}';
	}
}
