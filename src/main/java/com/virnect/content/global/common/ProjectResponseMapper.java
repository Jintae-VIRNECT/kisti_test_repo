package com.virnect.content.global.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectTarget;
import com.virnect.content.dto.response.ProjectInfoResponse;
import com.virnect.content.dto.response.ProjectTargetInfoResponse;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-21
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Mapper(componentModel = "spring")
public interface ProjectResponseMapper {
	@Mapping(target = "uploaderUUID", source = "userUUID")
	ProjectInfoResponse projectToProjectInfoResponse(Project project);

	ProjectTargetInfoResponse projectTargetToTargetInfoResponse(ProjectTarget projectTarget);
}
