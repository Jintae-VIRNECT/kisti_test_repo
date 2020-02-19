package com.virnect.workspace.dto.response;

import com.virnect.workspace.domain.Workspace;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class WorkspaceCreateResponse {
    @ApiModelProperty(value = "컨텐츠 정보 배열", notes = "조회된 컨텐츠들의 정보가 담긴 배열입니다.")
    private final Workspace newWorkspace;
}
