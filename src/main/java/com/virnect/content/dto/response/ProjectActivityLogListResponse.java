package com.virnect.content.dto.response;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.content.domain.project.ProjectActivityLog;
import com.virnect.content.global.common.PageMetadataResponse;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@ApiModel
@RequiredArgsConstructor
@Getter
public class ProjectActivityLogListResponse {
	@ApiModelProperty(value = "프로젝트 활동 이력 목록", notes = "프로젝트 활동 이력 목록입니다.", position = 0)
	private final List<ProjectActivityLogResponse> projectActivityLogList;
	@ApiModelProperty(value = "페이징 정보", notes = "페이지네이션을 하기 위해 필요한 정보입니다.", position = 1)
	private final PageMetadataResponse pageMeta;
}
