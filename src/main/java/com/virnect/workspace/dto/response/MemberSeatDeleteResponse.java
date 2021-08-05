package com.virnect.workspace.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Project: PF-Workspace
 * DATE: 2021-08-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class MemberSeatDeleteResponse {
    private final boolean result;
    private final LocalDateTime deletedDate;
}
