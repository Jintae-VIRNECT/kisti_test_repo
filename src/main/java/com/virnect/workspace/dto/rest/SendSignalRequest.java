package com.virnect.workspace.dto.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Workspace
 * DATE: 2021-12-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class SendSignalRequest {
	private final String sessionId;
	private final String type;
	private final String data;

	@Override
	public String toString() {
		return "SendSignalRequest{" +
			"sessionId='" + sessionId + '\'' +
			", type='" + type + '\'' +
			", data='" + data + '\'' +
			'}';
	}
}
