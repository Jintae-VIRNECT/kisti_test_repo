package com.virnect.workspace.application.workspaceuser.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.license.dto.UserLicenseInfo;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.application.user.UserRestServiceHandler;
import com.virnect.workspace.application.user.dto.response.UserInfoListRestResponse;
import com.virnect.workspace.application.user.dto.response.UserInfoRestResponse;
import com.virnect.workspace.dao.workspaceuser.WorkspaceUserRepository;
import com.virnect.workspace.dao.workspaceuserpermission.WorkspaceUserPermissionRepository;
import com.virnect.workspace.domain.workspace.Role;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.response.PageMetadataRestResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.global.common.PageRequest;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkspaceUserServiceCustom {

	private final WorkspaceUserRepository workspaceUserRepository;
	private final UserRestService userRestService;
	private final RestMapStruct restMapStruct;
	private final UserRestServiceHandler userRestServiceHandler;
	private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
	private final LicenseRestService licenseRestService;

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

	public WorkspaceUserInfoListResponse getMembers(
		String workspaceId, String search, String filter, List<Role> roleFilter, String userTypeFilter,
		String planFilter, PageRequest pageRequest, boolean paging
	) {
		//1. 정렬 검증으로 Pageable 재정의
		Pageable newPageable = pageRequest.of();

		List<String> resultUserIdList = workspaceUserRepository.getWorkspaceUserIdList(workspaceId);//워크스페이스 소속 전체 유저

		if (StringUtils.hasText(filter)) {
			//3-1. 라이선스 플랜으로 필터링
			if (filter.matches(".*(?i)REMOTE.*|.*(?i)MAKE.*|.*(?i)VIEW.*") && !resultUserIdList.isEmpty()) {
				resultUserIdList = filterUserIdListByPlan(workspaceId, resultUserIdList, filter);
			}
			//3-2. 워크스페이스 역할로 필터링
			else if (filter.matches(".*(?i)MASTER.*|.*(?i)MANAGER.*|.*(?i)MEMBER.*|.*(?i)GUEST.*")
				&& !resultUserIdList.isEmpty()) {
				String[] filters = filter.toUpperCase().split(",").length == 0 ? new String[] {filter.toUpperCase()} :
					filter.toUpperCase().split(",");
				List<Role> roleList = new ArrayList<>();
				Arrays.stream(Role.values()).forEach(role -> {
					for (String StringRole : filters) {
						if (role.name().equals(StringRole)) {
							roleList.add(role);
						}
					}
				});
				resultUserIdList = workspaceUserPermissionRepository.getUserIdsByInUserListAndEqRole(
					resultUserIdList, roleList, workspaceId);
			}
		}

		if (StringUtils.hasText(search)) {
			UserInfoListRestResponse userInfoListRestResponse = userRestServiceHandler.getUserListRequest(
				search, resultUserIdList);
			resultUserIdList = userInfoListRestResponse.getUserInfoList()
				.stream()
				.map(UserInfoRestResponse::getUuid)
				.collect(Collectors.toList());
		}

		//3-3. 워크스페이스 역할로 필터링
		if (!CollectionUtils.isEmpty(roleFilter)) {
			resultUserIdList = workspaceUserPermissionRepository.getUserIdsByInUserListAndEqRole(
				resultUserIdList, roleFilter, workspaceId);
		}

		//3-4. 유저 타입으로 필터링
		if (StringUtils.hasText(userTypeFilter)) {
			UserInfoListRestResponse userInfoListRestResponse = userRestServiceHandler.getUserListRequest(
				null, resultUserIdList);
			String[] userTypes =
				userTypeFilter.toUpperCase().split(",").length == 0 ? new String[] {userTypeFilter.toUpperCase()} :
					userTypeFilter.toUpperCase().split(",");
			List<String> filteredUserIdList = new ArrayList<>();
			userInfoListRestResponse.getUserInfoList().forEach(userInfoRestResponse -> {
				if (Arrays.stream(userTypes).anyMatch(s -> s.equals(userInfoRestResponse.getUserType().name()))) {
					filteredUserIdList.add(userInfoRestResponse.getUuid());
				}
			});
			resultUserIdList = filteredUserIdList;
		}

		//3-5. 라이선스 플랜으로 필터링
		if (StringUtils.hasText(planFilter)) {
			resultUserIdList = filterUserIdListByPlan(workspaceId, resultUserIdList, planFilter);
		}

		//4. 결과가 없는 경우에는 빠른 리턴
		if (resultUserIdList.isEmpty()) {
			PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
			pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
			pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
			return new WorkspaceUserInfoListResponse();
		}

		//5-1. 최종적으로 필터링된 유저들을 조회한다.
		if (!paging) {
			List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.getWorkspaceUserListByInUserList(
				resultUserIdList, workspaceId);
			List<WorkspaceUserInfoResponse> workspaceUserListResponse = generateWorkspaceUserListResponse(
				workspaceId,
				workspaceUserPermissionList
			);
			return new WorkspaceUserInfoListResponse(workspaceUserListResponse);
		}

		//5-2. 최종적으로 필터링된 유저들을 페이징한다.
		Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.getWorkspaceUserPageByInUserList(
			resultUserIdList, newPageable, workspaceId);
		List<WorkspaceUserInfoResponse> workspaceUserListResponse = generateWorkspaceUserListResponse(
			workspaceId, workspaceUserPermissionPage.toList());

		PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
		pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
		pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
		pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
		pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());

		if (pageRequest.getSortName().equalsIgnoreCase("email") || pageRequest.getSortName()
			.equalsIgnoreCase("nickname")) {
			return new WorkspaceUserInfoListResponse(
				getSortedMemberList(pageRequest, workspaceUserListResponse), pageMetadataResponse);
		}

		return new WorkspaceUserInfoListResponse(workspaceUserListResponse, pageMetadataResponse);
	}

	public List<String> filterUserIdListByPlan(String workspaceId, List<String> userIdList, String planFilter) {

		List<UserLicenseInfo> userLicenseInfoList = licenseRestService.getUserLicenseInfoList(
			workspaceId, userIdList, planFilter).getData().getUserLicenseInfos();

		return userLicenseInfoList.stream()
			.filter(
				userLicenseInfo -> userLicenseInfo.getProductName().toUpperCase().contains(planFilter.toUpperCase()))
			.map(UserLicenseInfo::getUserId)
			.collect(Collectors.toList());
	}

	public List<WorkspaceUserInfoResponse> generateWorkspaceUserListResponse(
		String workspaceId, List<WorkspaceUserPermission> workspaceUserPermissionList
	) {
		List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = new ArrayList<>();

		List<String> workspaceUserIdList = workspaceUserPermissionList.stream()
			.map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId())
			.collect(Collectors.toList());

		//license
		List<UserLicenseInfo> userLicenseInfoList = licenseRestService.getUserLicenseInfoList(
			workspaceId, workspaceUserIdList, "").getData().getUserLicenseInfos();

		//user
		List<UserInfoRestResponse> userInfoListRestResponse = userRestServiceHandler.getUserListRequest(
			"", workspaceUserIdList).getUserInfoList();

		for (UserInfoRestResponse userInfoRestResponse : userInfoListRestResponse) {
			WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(
				userInfoRestResponse);
			workspaceUserInfoResponseList.add(workspaceUserInfoResponse);
		}

		for (UserLicenseInfo userLicenseInfo : userLicenseInfoList) {
			for (WorkspaceUserInfoResponse workspaceUserInfoResponse : workspaceUserInfoResponseList) {

				// 유저 라이센스 할당.
				if (workspaceUserInfoResponse.getUuid().equals(userLicenseInfo.getUserId())) {
					setUserLicenseProducForWorkspaceUserInfoResponse(userLicenseInfo, workspaceUserInfoResponse);
				}
				// 라이센스가 할당되지 않은 유저 빈 배열 return
				if (ArrayUtils.isEmpty(workspaceUserInfoResponse.getLicenseProducts())) {
					workspaceUserInfoResponse.setLicenseProducts(new String[0]);
				}
			}
		}

		// 유저 퍼미션에 대한 정보 maaping
		for (WorkspaceUserInfoResponse workspaceUserInfoResponse : workspaceUserInfoResponseList) {
			for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionList) {
				if (workspaceUserInfoResponse.getUuid()
					.equals(workspaceUserPermission.getWorkspaceUser().getUserId())) {
					workspaceUserInfoResponse.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
					workspaceUserInfoResponse.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
					workspaceUserInfoResponse.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
				}
			}
		}

		return workspaceUserInfoResponseList;
	}

	private void setUserLicenseProducForWorkspaceUserInfoResponse(
		UserLicenseInfo userLicenseInfo, WorkspaceUserInfoResponse workspaceUserInfoResponse
	) {
		String[] userLicenseProducts;

		// workspaceUserInfoResponse의 license 정보 비어있을 경우 license 할당.
		if (ArrayUtils.isEmpty(workspaceUserInfoResponse.getLicenseProducts())) {
			userLicenseProducts = new String[] {userLicenseInfo.getProductName()};

		} else {
			// workspaceUserInfoResponse의 license 정보에 1개 이상의 데이터가 있을 경우 array 추가.
			String[] addUserLicenseProducts = new String[] {userLicenseInfo.getProductName()};
			userLicenseProducts = Stream.of(
					workspaceUserInfoResponse.getLicenseProducts(), addUserLicenseProducts)
				.flatMap(Arrays::stream)
				.toArray(String[]::new);
		}
		workspaceUserInfoResponse.setLicenseProducts(userLicenseProducts);
	}

	public List<WorkspaceUserInfoResponse> getSortedMemberList(
		com.virnect.workspace.global.common.PageRequest
			pageRequest, List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList
	) {
		String sortName = pageRequest.getSortName();
		String sortDirection = pageRequest.getSortDirection();
		if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("asc")) {
			return workspaceUserInfoResponseList.stream()
				.sorted(Comparator.comparing(
					WorkspaceUserInfoResponse::getEmail,
					Comparator.nullsFirst(Comparator.naturalOrder())
				))
				.collect(Collectors.toList());
		}
		if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("desc")) {
			return workspaceUserInfoResponseList.stream()
				.sorted(Comparator.comparing(
					WorkspaceUserInfoResponse::getEmail,
					Comparator.nullsFirst(Comparator.reverseOrder())
				))
				.collect(Collectors.toList());
		}
		if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("asc")) {
			List<WorkspaceUserInfoResponse> koList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1)
					.matches("[가-힣\\s]"))
				.sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName))
				.collect(Collectors.toList());
			List<WorkspaceUserInfoResponse> enList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1)
					.matches("[a-zA-Z\\s]"))
				.sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName))
				.collect(Collectors.toList());
			List<WorkspaceUserInfoResponse> etcList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> !koList.contains(memberInfoDTO))
				.filter(memberInfoDTO -> !enList.contains(memberInfoDTO))
				.sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName))
				.collect(Collectors.toList());
			List<WorkspaceUserInfoResponse> nullList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> !StringUtils.hasText(memberInfoDTO.getNickName()))
				.collect(Collectors.toList());
			enList.addAll(etcList);
			koList.addAll(enList);
			nullList.addAll(koList);
			return nullList;
		}
		if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("desc")) {
			List<WorkspaceUserInfoResponse> koList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1)
					.matches("[가-힣\\s]"))
				.sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName).reversed())
				.collect(Collectors.toList());
			List<WorkspaceUserInfoResponse> enList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1)
					.matches("[a-zA-Z\\s]"))
				.sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName).reversed())
				.collect(Collectors.toList());
			List<WorkspaceUserInfoResponse> etcList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> !koList.contains(memberInfoDTO))
				.filter(memberInfoDTO -> !enList.contains(memberInfoDTO))
				.sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName).reversed())
				.collect(Collectors.toList());
			List<WorkspaceUserInfoResponse> nullList = workspaceUserInfoResponseList.stream()
				.filter(memberInfoDTO -> !StringUtils.hasText(memberInfoDTO.getNickName()))
				.collect(Collectors.toList());
			enList.addAll(etcList);
			koList.addAll(enList);
			nullList.addAll(koList);
			return nullList;
		} else {
			return workspaceUserInfoResponseList.stream()
				.sorted(Comparator.comparing(
					WorkspaceUserInfoResponse::getUpdatedDate,
					Comparator.nullsFirst(Comparator.reverseOrder())
				))
				.collect(Collectors.toList());
		}
	}

}