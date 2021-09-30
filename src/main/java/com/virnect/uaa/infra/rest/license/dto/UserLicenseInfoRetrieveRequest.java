package com.virnect.uaa.infra.rest.license.dto;

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
	private String workspaceId;
	private List<String> userIds = new ArrayList<>();
	private String product;

	@Override
	public String toString() {
		return "UserLicenseInfoRetrieveRequest{" +
			"workspaceId='" + workspaceId + '\'' +
			", userIds=" + userIds +
			'}';
	}
}
