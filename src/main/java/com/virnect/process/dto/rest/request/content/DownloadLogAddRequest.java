package com.virnect.process.dto.rest.request.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-02-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public class DownloadLogAddRequest {
	@ApiModelProperty(value = "컨텐츠 식별자", example = "2f10b4f0-64ef-474a-ac0b-0362b4e345c9")
	private final String contentUUID;
	@ApiModelProperty(value = "유저 식별자", example = "42ca762ee245403ea35743fd8a690918")
	private final String memberUUID;
}
