package com.virnect.uaa.infra.rest.license.dto;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LicenseSecessionResponse {
	private final String workspaceUUID;
	private final boolean result;
	private final LocalDateTime deletedDate;

	@Override
	public String toString() {
		return "LicenseSecessionResponse{" +
			"workspaceUUID='" + workspaceUUID + '\'' +
			", result=" + result +
			", deletedDate=" + deletedDate +
			'}';
	}
}
