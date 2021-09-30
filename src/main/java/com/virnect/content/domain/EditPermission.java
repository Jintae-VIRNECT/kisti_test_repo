package com.virnect.content.domain;

import javax.validation.Valid;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Valid
public enum EditPermission {
	MEMBER,
	SPECIFIC_MEMBER,
	UPLOADER,
	MANAGER
}
