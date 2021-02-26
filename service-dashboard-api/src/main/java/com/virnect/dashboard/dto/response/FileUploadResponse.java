package com.virnect.dashboard.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class FileUploadResponse {
	@ApiModelProperty(value = "Workspace id")
	private String workspaceId;

	@ApiModelProperty(value = "Seesion id", position = 1)
	private String sessionId;

	@ApiModelProperty(value = "User id", position = 2)
	private String userId;

	@ApiModelProperty(value = "file name", position = 3)
	private String name;

	@ApiModelProperty(value = "object name", position = 4, example = "2020-08-28_fjxqMrTwIcVmxFIjgfRC")
	private String objectName;

	@ApiModelProperty(value = "content type", position = 5, example = "image/png")
	private String contentType;

	@ApiModelProperty(value = "file size", position = 6)
	private Long size;

	@Override
	public String toString() {
		return "FileUploadResponse{" +
			"workspaceId='" + workspaceId + '\'' +
			", sessionId='" + sessionId + '\'' +
			", userId='" + userId + '\'' +
			", name='" + name + '\'' +
			", objectName='" + objectName + '\'' +
			", contentType='" + contentType + '\'' +
			", size='" + size + '\'' +
			'}';
	}
}
