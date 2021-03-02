package com.virnect.dashboard.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FileDownloadRequest {
	@ApiModelProperty(value = "Workspace id", example = "40f9bbee9d85dca7a34a0dd205aae718")
	@NotBlank
	private String workspaceId;

	@ApiModelProperty(value = "Seesion id")
	@NotBlank
	private String sessionId;

	@ApiModelProperty(value = "user id", example = "410df50ca6e32db0b6acba09bcb457ff")
	@NotBlank
	private String uuid;

	@NotNull
	private String fileName;

	@Override
	public String toString() {
		return "FileDownloadRequest{" +
			"workspaceId='" + workspaceId + '\'' +
			", sessionId='" + sessionId + '\'' +
			", userId='" + uuid + '\'' +
			", file='" + fileName + '\'' +
			'}';
	}
}
