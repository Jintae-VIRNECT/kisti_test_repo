package com.virnect.workspace.dto.response;

import com.virnect.workspace.dto.MemberInfoDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class MemberListResponse {
    private final List<MemberInfoDTO> memberInfoList;
    private final PageMetadataResponse pageMeta;
}
