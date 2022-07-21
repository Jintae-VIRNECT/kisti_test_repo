package com.virnect.workspace.application.workspaceuser.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.application.user.dto.response.UserInfoListRestResponse;
import com.virnect.workspace.dao.workspaceuser.WorkspaceUserRepository;
import com.virnect.workspace.domain.workspace.Role;
import com.virnect.workspace.dto.response.WorkspaceUserInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;

import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class  WorkspaceUserServiceCustom {

	private final WorkspaceUserRepository workspaceUserRepository;
	private final UserRestService userRestService;
	private final RestMapStruct restMapStruct;

	public WorkspaceUserInfoListResponse getSimpleWorkspaceUserList(String workspaceId) {

		List<String> workspaceUserIdList = workspaceUserRepository.getWorkspaceUserIdList(workspaceId);
		UserInfoListRestResponse userInfoListRestResponse = getUserInfoList("", workspaceUserIdList);
		List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = userInfoListRestResponse.getUserInfoList()
			.stream()
			.map(userInfoRestResponse -> {
				WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(
					userInfoRestResponse);
				workspaceUserInfoResponse.setRole(Role.MEMBER);
				workspaceUserInfoResponse.setRoleId(3L);
				return workspaceUserInfoResponse;
			})
			.collect(Collectors.toList());

		return new WorkspaceUserInfoListResponse(workspaceUserInfoResponseList, null);
	}

	private UserInfoListRestResponse getUserInfoList(String search, List<String> workspaceUserIdList) {

		return userRestService.getUserInfoList(search, workspaceUserIdList).getData();
	}

}