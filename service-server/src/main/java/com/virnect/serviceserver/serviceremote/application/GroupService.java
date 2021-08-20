package com.virnect.serviceserver.serviceremote.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.group.FavoriteGroupRepository;
import com.virnect.data.dao.group.RemoteGroupRepository;
import com.virnect.data.domain.group.FavoriteGroup;
import com.virnect.data.domain.group.FavoriteGroupMember;
import com.virnect.data.domain.group.RemoteGroup;
import com.virnect.data.domain.group.RemoteGroupMember;
import com.virnect.data.dto.request.member.GroupRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.group.FavoriteGroupListResponse;
import com.virnect.data.dto.response.group.FavoriteGroupMemberResponse;
import com.virnect.data.dto.response.group.FavoriteGroupResponse;
import com.virnect.data.dto.response.group.RemoteGroupListResponse;
import com.virnect.data.dto.response.group.RemoteGroupMemberResponse;
import com.virnect.data.dto.response.group.RemoteGroupResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.serviceserver.serviceremote.dto.mapper.group.FavoriteGroupMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.group.FavoriteGroupMemberMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.group.RemoteGroupMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.group.RemoteGroupMemberMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

	private final WorkspaceRestService workspaceRestService;

	private final RemoteGroupRepository groupRepository;
	private final FavoriteGroupRepository favoriteGroupRepository;

	private final RemoteGroupMapper remoteGroupMapper;
	private final RemoteGroupMemberMapper remoteGroupMemberMapper;

	private final FavoriteGroupMapper favoriteGroupMapper;
	private final FavoriteGroupMemberMapper favoriteGroupMemberMapper;

	private final AccessStatusService accessStatusService;

	public ApiResponse<RemoteGroupResponse> createRemoteGroup(
		String workspaceId,
		String userId,
		GroupRequest groupRequest
	) {
		// 권한 확인 (Only Master)
		if (!checkMaster(workspaceId, userId)) {
			return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
		}

		long groupCount = groupRepository.findByWorkspaceIdGroupCount(workspaceId);
		if (groupCount >= 10) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_COUNT_OVER);
		}
		if (groupRequest.getMemberList().size() > 6) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_MEMBER_COUNT_OVER);
		}

		RemoteGroup remoteGroup = RemoteGroup.builder()
			.workspaceId(workspaceId)
			.groupId("group_" + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphanumeric(9))
			.groupName(groupRequest.getGroupName())
			.build();

		for (String uuid : groupRequest.getMemberList()) {
			RemoteGroupMember groupMember = RemoteGroupMember.builder()
				.remoteGroup(remoteGroup)
				.uuid(uuid)
				.build();
			remoteGroup.getGroupMembers().add(groupMember);
		}

		RemoteGroup result = groupRepository.save(remoteGroup);
		if (ObjectUtils.isEmpty(result)) {
			return new ApiResponse<>(ErrorCode.ERR_DATA_SAVE_EXCEPTION);
		}

		return new ApiResponse<>(setGroupMembersInfo(result));
	}

	public ApiResponse<RemoteGroupListResponse> getRemoteGroups(
		String workspaceId
	) {

		List<RemoteGroup> remoteGroups = groupRepository.findByWorkspaceId(workspaceId);
		if (CollectionUtils.isEmpty(remoteGroups)) {
			return new ApiResponse<>(
				RemoteGroupListResponse.builder().groupInfoResponseList(Collections.emptyList()).build()
			);
		}

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (RemoteGroup remoteGroup : remoteGroups) {
			for (RemoteGroupMember groupMember : remoteGroup.getGroupMembers()) {
				if (!(StringUtils.isBlank(groupMember.getUuid()))) {
					userList.add(groupMember.getUuid());
				}
			}
		}

		// Workspace 내 멤버 정보 가져 오기
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId,
			userList.stream().distinct().toArray(String[]::new)
		);

		List<RemoteGroupResponse> groupInfoResponseList = new ArrayList<>();

		// Add default group
		// 워크스페이스 전체 멤버에서 그룹 멤버에 있는 멤버 제외한 데이터


		for (RemoteGroup remoteGroup : remoteGroups) {
			RemoteGroupResponse remoteGroupResponse = remoteGroupMapper.toDto(remoteGroup);
			// Mapping Member List Data to Member Information List
			List<RemoteGroupMemberResponse> groupMembersInfo = remoteGroup.getGroupMembers().stream()
				.map(remoteGroupMemberMapper::toDto)
				.collect(Collectors.toList());
			// Set nick name, profile in workspace members info
			for (RemoteGroupMemberResponse groupMember : groupMembersInfo) {
				for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
					if (groupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
						groupMember.setNickName(workspaceMemberInfoResponse.getNickName());
						groupMember.setProfile(workspaceMemberInfoResponse.getProfile());
					}
				}
			}

			remoteGroupResponse.setRemoteGroupMemberResponses(groupMembersInfo);
			groupInfoResponseList.add(remoteGroupResponse);
		}

		return new ApiResponse<>(RemoteGroupListResponse.builder().groupInfoResponseList(groupInfoResponseList).build());
	}

	public ApiResponse<RemoteGroupResponse> getRemoteGroupDetailInfo(
		String workspaceId,
		String groupId,
		String filter,
		String search,
		boolean accessTypeFilter
	) {
		RemoteGroup remoteGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId);
		if (ObjectUtils.isEmpty(remoteGroup)) {
			return new ApiResponse<>(new RemoteGroupResponse());
		}

		WorkspaceMemberInfoListResponse memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, 0, Integer.MAX_VALUE).getData();

		RemoteGroupResponse remoteGroupInfoResponse = remoteGroupMapper.toDto(remoteGroup);
		// Mapping Member List Data to Member Information List
		List<RemoteGroupMemberResponse> groupMembersInfo = remoteGroup.getGroupMembers().stream()
			.map(remoteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (RemoteGroupMemberResponse groupMember : groupMembersInfo) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getMemberInfoList()) {
				if (groupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					groupMember.setNickName(workspaceMemberInfoResponse.getNickName());
					groupMember.setProfile(workspaceMemberInfoResponse.getProfile());

					// 접속 상태 확인
					groupMember.setAccessType(loadAccessType(workspaceId, groupMember.getUuid()));
				}
			}
		}

		if (accessTypeFilter) {
			for(Iterator<RemoteGroupMemberResponse> groupMemberIterator = groupMembersInfo.iterator(); groupMemberIterator.hasNext();){
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId + "_" + groupMemberIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					groupMemberIterator.remove();
				}
			}
		}

		remoteGroupInfoResponse.setRemoteGroupMemberResponses(groupMembersInfo);
		return new ApiResponse<>(remoteGroupInfoResponse);
	}

	public ApiResponse<RemoteGroupResponse> updateRemoteGroup(
		String workspaceId,
		String userId,
		String groupId,
		GroupRequest groupRequest
	) {

		if (!checkMaster(workspaceId, userId)) {
			return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
		}

		RemoteGroup targetGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId);
		if (ObjectUtils.isEmpty(targetGroup)) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_NOT_FOUND);
		}

		if (!CollectionUtils.isEmpty(groupRequest.getMemberList())) {
			// 기존 그룹 멤버 테이블 데이터 삭제
			targetGroup.getGroupMembers().clear();
			// 신규 그룹 멤버 데이터 추가
			for (String uuid : groupRequest.getMemberList()) {
				RemoteGroupMember groupMember = RemoteGroupMember.builder()
					.remoteGroup(targetGroup)
					.uuid(uuid)
					.build();
				targetGroup.getGroupMembers().add(groupMember);
			}
		}
		targetGroup.setGroupName(groupRequest.getGroupName());
		RemoteGroup result = groupRepository.save(targetGroup);
		if (ObjectUtils.isEmpty(result)) {
			return new ApiResponse<>(ErrorCode.ERR_DATA_SAVE_EXCEPTION);
		}
		return new ApiResponse<>(setGroupMembersInfo(result));
	}

	public ApiResponse<ResultResponse> deleteRemoteGroup(
		String workspaceId,
		String userId,
		String groupId
	) {
		if (!checkMaster(workspaceId, userId)) {
			return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
		}

		RemoteGroup targetGroup = groupRepository.findByWorkspaceIdAndGroupIdAndUserId(workspaceId, groupId, userId);
		if (ObjectUtils.isEmpty(targetGroup)) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_NOT_FOUND);
		}

		groupRepository.delete(targetGroup);

		ResultResponse resultResponse = new ResultResponse();
		resultResponse.userId = userId;
		resultResponse.setResult(true);
		return new ApiResponse<>(resultResponse);
	}



	private boolean checkMaster(String workspaceId, String userId) {
		// Master uuid 및 권한 체크
		WorkspaceMemberInfoResponse masterUserInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, userId).getData();
		if (!("MASTER".equals(masterUserInfo.getRole()))) {
			return false;
		}
		return true;
	}

	private RemoteGroupResponse setGroupMembersInfo(RemoteGroup remoteGroup) {

		RemoteGroupResponse remoteGroupInfoResponse = remoteGroupMapper.toDto(remoteGroup);
		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (RemoteGroupMember groupMember : remoteGroup.getGroupMembers()) {
			if (!(StringUtils.isBlank(groupMember.getUuid()))) {
				userList.add(groupMember.getUuid());
			}
		}

		// Workspace 내 멤버 정보 가져 오기
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			remoteGroup.getWorkspaceId(),
			userList.stream().distinct().toArray(String[]::new)
		);

		// Mapping Member List Data to Member Information List
		List<RemoteGroupMemberResponse> groupMembersInfo = remoteGroup.getGroupMembers().stream()
			.map(remoteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (RemoteGroupMemberResponse remoteGroupMemberInfoResponse : groupMembersInfo) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
				if (remoteGroupMemberInfoResponse.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					remoteGroupMemberInfoResponse.setNickName(workspaceMemberInfoResponse.getNickName());
					remoteGroupMemberInfoResponse.setProfile(workspaceMemberInfoResponse.getProfile());
				}
			}
		}
		remoteGroupInfoResponse.setRemoteGroupMemberResponses(groupMembersInfo);

		return remoteGroupInfoResponse;
	}

	private AccessType loadAccessType(String workspaceId, String uuid) {
		AccessType result;
		try {
			AccessStatus accessStatus = accessStatusService.getAccessStatus(workspaceId + "_" + uuid);
			if (ObjectUtils.isEmpty(accessStatus) || accessStatus.getAccessType() == AccessType.LOGOUT) {
				result = AccessType.LOGOUT;
			} else {
				result = accessStatus.getAccessType();
			}
		} catch (Exception e) {
			log.info("SET MEMBER STATUS EXCEPTION (uuid, exception message) => [{}], [{}]", uuid, e.getMessage());
			result = AccessType.LOGOUT;
		}
		return result;
	}

	public ApiResponse<FavoriteGroupResponse> createFavoriteGroup(
		String workspaceId,
		String userId,
		GroupRequest groupRequest
	) {
		long groupCount = favoriteGroupRepository.findByWorkspaceIdAndUserIdGroupCount(workspaceId, userId);
		if (groupCount >= 10) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_COUNT_OVER);
		}
		if (groupRequest.getMemberList().size() > 6) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_MEMBER_COUNT_OVER);
		}

		FavoriteGroup favoriteGroup = FavoriteGroup.builder()
			.workspaceId(workspaceId)
			.groupId("group_" + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphanumeric(9))
			.groupName(groupRequest.getGroupName())
			.build();

		for (String uuid : groupRequest.getMemberList()) {
			FavoriteGroupMember favoriteGroupMember = FavoriteGroupMember.builder()
				.favoriteGroup(favoriteGroup)
				.uuid(uuid)
				.build();
			favoriteGroup.getFavoriteGroupMembers().add(favoriteGroupMember);
		}

		FavoriteGroup result = favoriteGroupRepository.save(favoriteGroup);
		if (ObjectUtils.isEmpty(result)) {
			return new ApiResponse<>(ErrorCode.ERR_DATA_SAVE_EXCEPTION);
		}


		FavoriteGroupResponse favoriteGroupResponse = favoriteGroupMapper.toDto(favoriteGroup);
		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (FavoriteGroupMember favoriteGroupMember : favoriteGroup.getFavoriteGroupMembers()) {
			if (!(StringUtils.isBlank(favoriteGroupMember.getUuid()))) {
				userList.add(favoriteGroupMember.getUuid());
			}
		}

		// Workspace 내 멤버 정보 가져 오기
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			favoriteGroup.getWorkspaceId(),
			userList.stream().distinct().toArray(String[]::new)
		);

		// Mapping Member List Data to Member Information List
		List<FavoriteGroupMemberResponse> groupMembersInfo = favoriteGroup.getFavoriteGroupMembers().stream()
			.map(favoriteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (FavoriteGroupMemberResponse remoteGroupMemberInfoResponse : groupMembersInfo) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
				if (remoteGroupMemberInfoResponse.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					remoteGroupMemberInfoResponse.setNickName(workspaceMemberInfoResponse.getNickName());
					remoteGroupMemberInfoResponse.setProfile(workspaceMemberInfoResponse.getProfile());
				}
			}
		}
		favoriteGroupResponse.setFavoriteGroupMemberResponses(groupMembersInfo);

		return new ApiResponse<>(favoriteGroupResponse);
	}

	public ApiResponse<FavoriteGroupListResponse> getFavoriteGroups(String workspaceId, String userId) {

		List<FavoriteGroup> favoriteGroups = favoriteGroupRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
		if (CollectionUtils.isEmpty(favoriteGroups)) {
			return new ApiResponse<>(
				FavoriteGroupListResponse.builder().favoriteGroupResponses(Collections.emptyList()).build()
			);
		}

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (FavoriteGroup favoriteGroup : favoriteGroups) {
			for (FavoriteGroupMember groupMember : favoriteGroup.getFavoriteGroupMembers()) {
				if (!(StringUtils.isBlank(groupMember.getUuid()))) {
					userList.add(groupMember.getUuid());
				}
			}
		}

		// Workspace 내 멤버 정보 가져 오기
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId,
			userList.stream().distinct().toArray(String[]::new)
		);

		List<FavoriteGroupResponse> groupInfoResponseList = new ArrayList<>();
		for (FavoriteGroup favoriteGroup : favoriteGroups) {
			FavoriteGroupResponse remoteGroupResponse = favoriteGroupMapper.toDto(favoriteGroup);
			// Mapping Member List Data to Member Information List
			List<FavoriteGroupMemberResponse> favoriteGroupMemberResponses = favoriteGroup.getFavoriteGroupMembers().stream()
				.map(favoriteGroupMemberMapper::toDto)
				.collect(Collectors.toList());
			// Set nick name, profile in workspace members info
			for (FavoriteGroupMemberResponse favoriteGroupMember : favoriteGroupMemberResponses) {
				for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
					if (favoriteGroupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
						favoriteGroupMember.setNickName(workspaceMemberInfoResponse.getNickName());
						favoriteGroupMember.setProfile(workspaceMemberInfoResponse.getProfile());
					}
				}
			}

			remoteGroupResponse.setFavoriteGroupMemberResponses(favoriteGroupMemberResponses);
			groupInfoResponseList.add(remoteGroupResponse);
		}
		return new ApiResponse<>(FavoriteGroupListResponse.builder().favoriteGroupResponses(groupInfoResponseList).build());
	}

	public ApiResponse<FavoriteGroupResponse> getFavoriteGroupDetailInfo(
		String workspaceId,
		String groupId,
		String filter,
		String search,
		boolean accessTypeFilter
	) {
		FavoriteGroup favoriteGroup = favoriteGroupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId);
		if (ObjectUtils.isEmpty(favoriteGroup)) {
			return new ApiResponse<>(new FavoriteGroupResponse());
		}

		WorkspaceMemberInfoListResponse memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, 0, Integer.MAX_VALUE).getData();

		FavoriteGroupResponse favoriteGroupResponse = favoriteGroupMapper.toDto(favoriteGroup);
		// Mapping Member List Data to Member Information List
		List<FavoriteGroupMemberResponse> favoriteGroupMemberResponses = favoriteGroup.getFavoriteGroupMembers().stream()
			.map(favoriteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (FavoriteGroupMemberResponse favoriteGroupMember : favoriteGroupMemberResponses) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getMemberInfoList()) {
				if (favoriteGroupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					favoriteGroupMember.setNickName(workspaceMemberInfoResponse.getNickName());
					favoriteGroupMember.setProfile(workspaceMemberInfoResponse.getProfile());

					// 접속 상태 확인
					favoriteGroupMember.setAccessType(loadAccessType(workspaceId, favoriteGroupMember.getUuid()));
				}
			}
		}
		if (accessTypeFilter) {
			for(Iterator<FavoriteGroupMemberResponse> groupMemberIterator = favoriteGroupMemberResponses.iterator(); groupMemberIterator.hasNext();){
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId + "_" + groupMemberIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					groupMemberIterator.remove();
				}
			}
		}
		favoriteGroupResponse.setFavoriteGroupMemberResponses(favoriteGroupMemberResponses);
		return new ApiResponse<>(favoriteGroupResponse);
	}

	public ApiResponse<FavoriteGroupResponse> updateFavoriteGroup(
		String workspaceId,
		String userId,
		String groupId,
		GroupRequest groupRequest
	) {
		FavoriteGroup targetGroup = favoriteGroupRepository.findByWorkspaceIdAndUserIdAndGroupId(workspaceId, userId, groupId);
		if (ObjectUtils.isEmpty(targetGroup)) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_NOT_FOUND);
		}

		if (!CollectionUtils.isEmpty(groupRequest.getMemberList())) {
			// 기존 그룹 멤버 테이블 데이터 삭제
			targetGroup.getFavoriteGroupMembers().clear();
			// 신규 그룹 멤버 데이터 추가
			for (String uuid : groupRequest.getMemberList()) {
				FavoriteGroupMember groupMember = FavoriteGroupMember.builder()
					.favoriteGroup(targetGroup)
					.uuid(uuid)
					.build();
				targetGroup.getFavoriteGroupMembers().add(groupMember);
			}
		}
		targetGroup.setGroupName(groupRequest.getGroupName());
		FavoriteGroup updatedFavoriteGroup = favoriteGroupRepository.save(targetGroup);
		if (ObjectUtils.isEmpty(updatedFavoriteGroup)) {
			return new ApiResponse<>(ErrorCode.ERR_DATA_SAVE_EXCEPTION);
		}

		FavoriteGroupResponse favoriteGroupResponse = favoriteGroupMapper.toDto(updatedFavoriteGroup);
		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (FavoriteGroupMember groupMember : updatedFavoriteGroup.getFavoriteGroupMembers()) {
			if (!(StringUtils.isBlank(groupMember.getUuid()))) {
				userList.add(groupMember.getUuid());
			}
		}

		// Workspace 내 멤버 정보 가져 오기
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			updatedFavoriteGroup.getWorkspaceId(),
			userList.stream().distinct().toArray(String[]::new)
		);

		// Mapping Member List Data to Member Information List
		List<FavoriteGroupMemberResponse> favoriteGroupMembers = updatedFavoriteGroup.getFavoriteGroupMembers().stream()
			.map(favoriteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (FavoriteGroupMemberResponse favoriteGroupMember : favoriteGroupMembers) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
				if (favoriteGroupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					favoriteGroupMember.setNickName(workspaceMemberInfoResponse.getNickName());
					favoriteGroupMember.setProfile(workspaceMemberInfoResponse.getProfile());
				}
			}
		}
		favoriteGroupResponse.setFavoriteGroupMemberResponses(favoriteGroupMembers);
		return new ApiResponse<>(favoriteGroupResponse);
	}

	public ApiResponse<ResultResponse> deleteFavoriteGroup(
		String workspaceId,
		String userId,
		String groupId
	) {
		FavoriteGroup targetGroup = favoriteGroupRepository.findByWorkspaceIdAndUserIdAndGroupId(workspaceId, userId, groupId);
		if (ObjectUtils.isEmpty(targetGroup)) {
			return new ApiResponse<>(ErrorCode.ERR_GROUP_NOT_FOUND);
		}

		favoriteGroupRepository.delete(targetGroup);

		ResultResponse resultResponse = new ResultResponse();
		resultResponse.userId = userId;
		resultResponse.setResult(true);
		return new ApiResponse<>(resultResponse);
	}
}
