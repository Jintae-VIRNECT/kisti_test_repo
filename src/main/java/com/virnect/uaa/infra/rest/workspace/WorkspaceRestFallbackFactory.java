package com.virnect.uaa.infra.rest.workspace;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoListResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceSecessionResponse;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.29
 */
@Slf4j
@Component
public class WorkspaceRestFallbackFactory implements FallbackFactory<WorkspaceRestService> {
	private static final String WORKSPACE_INVITE_ACCEPT_FALLBACK_MESSAGE = "[USER REGISTRATION][NON_MEMBER_REGISTRATION] ";

	@Override
	public WorkspaceRestService create(Throwable cause) {
		log.error(cause.getMessage(), cause);
		return new WorkspaceRestService() {
			@Override
			public ApiResponse<WorkspaceInfoListResponse> getMyWorkspaceInfoList(String userId, int size) {
				log.error("[USER WORKSPACE LIST API FALLBACK] => USER_ID: {}", userId);
				WorkspaceInfoListResponse empty = new WorkspaceInfoListResponse();
				empty.setWorkspaceList(new ArrayList<>());
				return new ApiResponse<>(empty);
			}

			@Override
			public ApiResponse<WorkspaceSecessionResponse> workspaceSecession(
				String service, String userUUID
			) {
				return new ApiResponse<>(new WorkspaceSecessionResponse(userUUID, false, LocalDateTime.now()));
			}

			@Override
			public void acceptInviteSession(String sessionCode, String lang) {
				log.error(WORKSPACE_INVITE_ACCEPT_FALLBACK_MESSAGE + "SESSION:[{}] , LANG:[{}]", sessionCode, lang);
				log.error(
					WORKSPACE_INVITE_ACCEPT_FALLBACK_MESSAGE + "- Fail to send session code to workspace for accept invite."
				);
			}
		};
	}
}
