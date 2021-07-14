package com.virnect.data.dto.response.file;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@Builder
public class FileStorageInfoResponse {
	private String workspaceId;
	private long profileStorageSize;
	private long shareFileStorageSize;
	private long attachFileStorageSize;
	private long totalRemoteUseStorageSize;
}
