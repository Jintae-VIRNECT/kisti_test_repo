package com.virnect.content.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.content.global.common.PageMetadataResponse;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
@ApiModel
public class ProjectInfoListResponse {
	@ApiModelProperty(value = "프로젝트 정보 목록", notes = "프로젝트 정보 목록입니다.")
	private final List<ProjectInfoResponse> projectInfoList;
	@ApiModelProperty(value = "페이징 정보", notes = "페이지네이션을 하기 위해 필요한 정보입니다.", position = 1)
	private final PageMetadataResponse pageMeta;
}
