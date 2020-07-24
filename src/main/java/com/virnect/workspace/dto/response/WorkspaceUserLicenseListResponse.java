package com.virnect.workspace.dto.response;

import com.virnect.workspace.dto.rest.PageMetadataRestResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-06-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@AllArgsConstructor
public class WorkspaceUserLicenseListResponse {
    public List<WorkspaceUserLicenseInfoResponse> workspaceUserLicenseInfoList;
    public PageMetadataRestResponse pageMeta;
}
