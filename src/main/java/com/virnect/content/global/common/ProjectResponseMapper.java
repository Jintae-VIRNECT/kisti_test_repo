package com.virnect.content.global.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectActivityLog;
import com.virnect.content.domain.project.ProjectTarget;
import com.virnect.content.dto.response.ProjectActivityLogResponse;
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
	ProjectInfoResponse projectToResponse(Project project);

	ProjectTargetInfoResponse projectTargetToResponse(ProjectTarget projectTarget);

	@Mapping(target = "name", expression = "java(projectActivityLog.getProject().getName())")
	@Mapping(target = "uuid", expression = "java(projectActivityLog.getProject().getUuid())")
	ProjectActivityLogResponse projectActivityLogToResponse(ProjectActivityLog projectActivityLog);
}
