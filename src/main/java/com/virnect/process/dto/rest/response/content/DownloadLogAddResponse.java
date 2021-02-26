package com.virnect.process.dto.rest.response.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-02-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public class DownloadLogAddResponse {
	@JsonProperty("downloadLogAddResult")
	private final boolean downloadLogAddResult;

	@JsonCreator
	public DownloadLogAddResponse(final boolean downloadLogAddResult) {
		this.downloadLogAddResult = downloadLogAddResult;
	}

}
