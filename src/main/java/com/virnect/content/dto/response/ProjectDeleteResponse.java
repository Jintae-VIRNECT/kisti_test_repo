package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-26
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class ProjectDeleteResponse {
	private final boolean result;
	private final String projectUUID;
	private final LocalDateTime deletedDate;
}
