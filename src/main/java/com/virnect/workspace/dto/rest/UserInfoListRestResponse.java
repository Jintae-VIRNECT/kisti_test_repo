package com.virnect.workspace.dto.rest;

import com.virnect.workspace.dto.response.PageMetadataResponse;
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
public class UserInfoListRestResponse {
    private final List<UserInfoRestResponse> userInfoList;
    private final PageMetadataResponse pageMeta;
}
