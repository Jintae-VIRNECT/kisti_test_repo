package com.virnect.workspace.global.common;

import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Mapper(componentModel = "spring")
public interface MapStructMapper {
    @Mapping(target = "nickName", source = "nickname")
    WorkspaceUserInfoResponse userInfoRestResponseToWorkspaceUserInfoResponse(UserInfoRestResponse userInfoRestResponse);
}
