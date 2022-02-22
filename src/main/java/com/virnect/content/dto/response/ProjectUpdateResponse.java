package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-31
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class ProjectUpdateResponse {
	@ApiModelProperty(value = "프로젝트 업데이트 결과", position = 0, example = "true")
	private final boolean result;
	@ApiModelProperty(value = "프로젝트 식별자", position = 1, example = "7a2a56ef-e349-42ca-abb6-96a99f2ce6f9")
	private final String projectUUID;
	@ApiModelProperty(value = "프로젝트 업로더 식별자", position = 2, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private final String uploaderUUID;
	@ApiModelProperty(value = "프로젝트 업로더 공유 권한 여부", position = 3, example = "true")
	private final boolean uploaderHasSharePermission;
	@ApiModelProperty(value = "프로젝트 업로더 편집 권한 여부", position = 4, example = "true")
	private final boolean uploaderHasEditPermission;
	@ApiModelProperty(value = "프로젝트 업데이트 일자", position = 5, example = "2020-02-15T16:32:13.305")
	private final LocalDateTime updatedDate;
}
