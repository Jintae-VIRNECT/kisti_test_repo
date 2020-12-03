package com.virnect.workspace.global.common;

import com.virnect.workspace.dto.rest.PageMetadataRestResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-11-11
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class CustomPageResponse<T> {
    private final List<T> afterPagingList;
    private final PageMetadataRestResponse pageMetadataResponse;
}
