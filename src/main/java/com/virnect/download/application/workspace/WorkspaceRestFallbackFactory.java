package com.virnect.download.application.workspace;

import java.util.Collections;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.dto.response.WorkspaceInfoListResponse;
import com.virnect.download.global.common.ApiResponse;

/**
 * Project: PF-Download
 * DATE: 2021-11-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class WorkspaceRestFallbackFactory implements FallbackFactory<WorkspaceRestService> {
	@Override
	public WorkspaceRestService create(Throwable cause) {
		return new WorkspaceRestService() {
			@Override
			public ApiResponse<WorkspaceInfoListResponse> getMyWorkspaceInfoList(String userUUID) {
				WorkspaceInfoListResponse workspaceInfoListResponse = new WorkspaceInfoListResponse();
				workspaceInfoListResponse.setWorkspaceList(Collections.emptyList());
				return new ApiResponse<>(workspaceInfoListResponse);
			}
		};
	}
}
