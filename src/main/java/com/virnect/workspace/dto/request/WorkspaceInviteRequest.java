package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceInviteRequest {
    @NotBlank
    private String userEmail;//초대할 유저
    private List<Long> workspacePermission;//소속 할당할 워크스페이스 정보
    private List<Map<String, String>> groups; //소속 할당할 그룹 정보
}
