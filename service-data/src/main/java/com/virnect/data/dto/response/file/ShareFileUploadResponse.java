package com.virnect.data.dto.response.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class ShareFileUploadResponse {

	@ApiModelProperty(value = "Workspace Identifier")
	private String workspaceId = "";

	@ApiModelProperty(value = "Session Identifier", position = 1)
	private String sessionId = "";

	@ApiModelProperty(value = "User Identifier", position = 2)
	private String uuid = "";

	@ApiModelProperty(value = "file name", position = 3)
	private String name = "";

	@ApiModelProperty(value = "object name", position = 4, example = "2020-08-28_fjxqMrTwIcVmxFIjgfRC")
	private String objectName = "";

	@ApiModelProperty(value = "content type", position = 5, example = "image/png")
	private String contentType = "";

	@ApiModelProperty(value = "file size", position = 6)
	private Long size = 0L;

	@ApiModelProperty(value = "삭제  유무", notes = "삭제 유무를 반환 합니다.", dataType = "Boolean", position = 7, example = "false")
	private boolean isDeleted;

	@ApiModelProperty(value = "만료 유무", notes = "만료 유무를 반환 합니다.", dataType = "Boolean", position = 8, example = "false")
	private boolean isExpired;

	@ApiModelProperty(value = "Thumbnail download url", position = 9)
	private String thumbnailDownloadUrl = "";

	@ApiModelProperty(value = "width", position = 10)
	private Integer width;

	@ApiModelProperty(value = "height", position = 11)
	private Integer height;

	@ApiModelProperty(value = "used storage size percent", position = 12)
	private int usedStoragePer;

	@Override
	public String toString() {
		return "ShareFileUploadResponse{" +
			"workspaceId='" + workspaceId + '\'' +
			", sessionId='" + sessionId + '\'' +
			", uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", objectName='" + objectName + '\'' +
			", contentType='" + contentType + '\'' +
			", size=" + size +
			", isDeleted=" + isDeleted +
			", isExpired=" + isExpired +
			", thumbnailDownloadUrl='" + thumbnailDownloadUrl + '\'' +
			", width=" + width +
			", height=" + height +
			", usedStoragePer=" + usedStoragePer +
			'}';
	}
}
