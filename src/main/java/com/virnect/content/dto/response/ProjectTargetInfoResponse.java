package com.virnect.content.dto.response;

import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.TargetType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-21
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ProjectTargetInfoResponse {
	private TargetType type;
	private long size;
	private String path;
}
