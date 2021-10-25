package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-06
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class FileResourceUploadResponse {
	@ApiModelProperty(value = "리소스 파일 경로", position = 0, example = "https://192.168.6.3:2838/virnect-platform/workspace/project/25e76918-e31a-420f-bd69-86a775c4a9ac.Ares")
	private final String uploadedUrl;
	@ApiModelProperty(value = "리소스 파일 업로드 일자", position = 1, example = "2020-02-15T16:32:13.305")
	private final LocalDateTime uploadedDate;
}
