package com.virnect.process.dto.rest.response.workspace;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-09-11
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class AllMemberListResponse {
	private final List<AllMemberInfoResponse> memberInfoList;
}
