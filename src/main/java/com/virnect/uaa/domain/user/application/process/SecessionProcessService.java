package com.virnect.uaa.domain.user.application.process;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.content.ContentRestService;
import com.virnect.uaa.infra.rest.content.dto.ContentSecessionResponse;
import com.virnect.uaa.infra.rest.license.LicenseRestService;
import com.virnect.uaa.infra.rest.license.dto.LicenseSecessionResponse;
import com.virnect.uaa.infra.rest.process.ProcessRestService;
import com.virnect.uaa.infra.rest.process.dto.TaskSecessionResponse;
import com.virnect.uaa.infra.rest.remote.RemoteRestService;
import com.virnect.uaa.infra.rest.remote.dto.RemoteSecessionResponse;
import com.virnect.uaa.infra.rest.workspace.WorkspaceRestService;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoListResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceSecessionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecessionProcessService {
	private final WorkspaceRestService workspaceRestService;
	private final ContentRestService contentRestService;
	private final ProcessRestService processRestService;
	private final RemoteRestService remoteRestService;
	private final LicenseRestService licenseRestService;

	public UserSecessionResponse sendSecessionRequestToAllExternalService(
		UserSecessionRequest secessionRequest, User user
	) {
		// 4. 내가 소속된 워크스페이스 목록 조회
		WorkspaceInfoListResponse workspaceInfoListResponses = getWorkspaceInfoResponseByUserId(user.getUuid());

		if (workspaceInfoListResponses == null || workspaceInfoListResponses.isEmpty()) {
			log.error("[WORKSPACE_MASTER_INFO_SECESSION] - "
					+ "userEmail: [{}] , userUUID: [{}] ->  workspaceInfoListResponse info is empty",
				secessionRequest.getEmail(), secessionRequest.getUuid()
			);
			return new UserSecessionResponse(user.getEmail(), user.getName(), LocalDateTime.now());
		}

		// 5. 워크스페이스 관련 정보 삭제 처리
		deleteSecessionUserRelatedWorkspaceInformation(user);

		// 6. 마스터 워크스페이스 관련 타 서비스 정보 삭제
		for (String masterWorkspaceUUID : workspaceInfoListResponses.getMasterPermissionWorkspaceUUIDList()) {
			deleteSecessionUserMasterWorkspaceRelatedInformation(user, masterWorkspaceUUID);
		}

		// 7. 라이선스 정보 삭제
		deleteSecessionUserLicenseInformation(user);

		return new UserSecessionResponse(user.getEmail(), user.getName(), LocalDateTime.now());
	}

	private void deleteSecessionUserLicenseInformation(User user) {
		ApiResponse<LicenseSecessionResponse> licenseResponse = licenseRestService.licenseSecession(
			"user-server", user.getUuid(), user.getId()
		);
		if (licenseResponse == null || !licenseResponse.getData().isResult()) {
			log.error("[License_SECESSION ERROR]");
		}
	}

	private void deleteSecessionUserMasterWorkspaceRelatedInformation(User user, String masterWorkspaceUUID) {
		// 1. 마스터 워크스페이스의 컨텐츠 파일 및 정보 삭제
		ApiResponse<ContentSecessionResponse> contentResponse = contentRestService.contentSecession(
			"user-server", masterWorkspaceUUID
		);
		if (contentResponse == null || !contentResponse.getData().isResult()) {
			log.error("[CONTENT_SECESSION ERROR]");
		}

		// 2. 마스터 워크스페이스의 공정 정보 삭제
		ApiResponse<TaskSecessionResponse> taskResponse = processRestService.taskSecession(
			"user-server", masterWorkspaceUUID, user.getUuid()
		);
		if (taskResponse == null || !taskResponse.getData().isResult()) {
			log.error("[TASK_SECESSION ERROR]");
		}

		// 3. 마스터 워크스페이스의 리모트 관련 정보 삭제
		ApiResponse<RemoteSecessionResponse> remoteResponse = remoteRestService.remoteUserSecession(user.getUuid());
		if (remoteResponse == null || !remoteResponse.getData().isResult()) {
			log.error("[REMOTE_SECESSION ERROR]");
		}
	}

	private void deleteSecessionUserRelatedWorkspaceInformation(User user) {
		ApiResponse<WorkspaceSecessionResponse> workspaceResponse = workspaceRestService.workspaceSecession(
			"user-server", user.getUuid()
		);

		if (workspaceResponse == null || !workspaceResponse.getData().isResult()) {
			log.error("[WORKSPACE_MEMBER_INFO_SECESSION ERROR]");
		}
		log.info("[WORKSPACE_MEMBER_INFO_SECESSION][SUCCESS] - {}", workspaceResponse);
	}

	private WorkspaceInfoListResponse getWorkspaceInfoResponseByUserId(String userUUID) {
		ApiResponse<WorkspaceInfoListResponse> workspaceApiResponse = workspaceRestService.getMyWorkspaceInfoList(
			userUUID, 50
		);
		if (workspaceApiResponse.getCode() != 200 || workspaceApiResponse.getData().getWorkspaceList().isEmpty()) {
			log.error("[WORKSPACE_INFO_NOT_FOUND] - {}", workspaceApiResponse.getMessage());
			return WorkspaceInfoListResponse.emptyResponse();
		}

		return workspaceApiResponse.getData();
	}

}
