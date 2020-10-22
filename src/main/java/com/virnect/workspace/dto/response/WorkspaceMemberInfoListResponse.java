package com.virnect.workspace.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.workspace.dto.MemberInfoDTO;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class WorkspaceMemberInfoListResponse {
	private final List<MemberInfoDTO> workspaceMemberInfoList;
}
