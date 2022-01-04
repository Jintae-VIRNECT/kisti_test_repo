package com.virnect.serviceserver.serviceremote.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.group.FavoriteGroupMemberRepository;
import com.virnect.data.dao.group.FavoriteGroupRepository;
import com.virnect.data.dao.group.RemoteGroupMemberRepository;
import com.virnect.data.dao.group.RemoteGroupRepository;
import com.virnect.data.domain.Role;
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
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RemoteServiceException;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.serviceserver.serviceremote.dto.mapper.group.WorkspaceToFavoriteGroupMemberMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.group.WorkspaceToRemoteGroupMemberMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

	private final String ETC_GROUP_ID = "group_etc";
	private final String ETC_GROUP_NAME = "기타";
	private final int GROUP_COUNT_LIMIT = 10;
	private final int GROUP_MEMBER_COUNT_LIMIT = 6;
	private final String GROUP = "group_";
	private final String RemoteLicense = "REMOTE";

	private final WorkspaceRestService workspaceRestService;

	private final RemoteGroupRepository groupRepository;
	private final RemoteGroupMemberRepository remoteGroupMemberRepository;
	private final FavoriteGroupRepository favoriteGroupRepository;
	private final FavoriteGroupMemberRepository favoriteGroupMemberRepository;

	private final WorkspaceToFavoriteGroupMemberMapper workspaceToFavoriteGroupMemberMapper;
	private final WorkspaceToRemoteGroupMemberMapper workspaceToRemoteGroupMemberMapper;

	private final AccessStatusService accessStatusService;

	public RemoteGroupResponse createRemoteGroup(
		String workspaceId,
		String userId,
		GroupRequest groupRequest
	) {
		if (!isMasterOfWorkspace(workspaceId, userId)) {
			throw new RemoteServiceException(ErrorCode.ERR_ACCESS_AUTHORITY);
		}
		if (!isUserIdsIncludeOfWorkspace(workspaceId, groupRequest.getMemberList())) {
			throw new RemoteServiceException(ErrorCode.ERR_MEMBER_INVALID);
		}
		if (CollectionUtils.isEmpty(groupRequest.getMemberList())) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_MEMBER_IS_EMPTY);
		}
		if (StringUtils.isBlank(groupRequest.getGroupName())) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_NAME_IS_NULL);
		}
		if (groupRepository.findByWorkspaceIdAndGroupName(workspaceId, groupRequest.getGroupName()) > 0) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_NAME_DUPLICATE);
		}
		if (groupRepository.findByWorkspaceIdAndUserIdArray(workspaceId, groupRequest.getMemberList()) > 0) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_MEMBER_ALREADY_JOINED);
		}

		RemoteGroup remoteGroup = buildRemoteGroup(workspaceId, groupRequest.getGroupName());
		for (String uuid : groupRequest.getMemberList()) {
			remoteGroup.getGroupMembers().add(buildRemoteGroupMember(remoteGroup, uuid));
		}

		groupRepository.save(remoteGroup);
		List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(
			workspaceId,
			remoteGroup.getGroupMembers()
				.stream()
				.map(RemoteGroupMember::getUuid)
				.collect(Collectors.toList())
		);

		List<RemoteGroupMemberResponse> remoteGroupMemberResponses = mapperWorkspaceToRemoteGroupMember(
			remoteGroup.getGroupMembers(), workspaceMembers);
		return buildRemoteGroupResponse(remoteGroup, remoteGroupMemberResponses);
	}

	public RemoteGroupListResponse getRemoteGroups(
		String workspaceId,
		String userId,
		boolean includeOneself
	) {
		List<RemoteGroup> remoteGroups = groupRepository.findByWorkspaceIdAndUserIdAndIncludeOneself(
			workspaceId, userId, includeOneself);

		List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(workspaceId);
		if (CollectionUtils.isEmpty(workspaceMembers)) {
			return buildRemoteGroupListResponse(new ArrayList<>(), 0);
		}
		workspaceMembers = includeOneselfFilter(userId, includeOneself, workspaceMembers);

		List<RemoteGroupResponse> groupsResponse = new ArrayList<>();
		for (RemoteGroup remoteGroup : remoteGroups) {
			List<RemoteGroupMemberResponse> remoteGroupMembers = mapperWorkspaceToRemoteGroupMember(
				remoteGroup.getGroupMembers(), workspaceMembers);

			for (RemoteGroupMemberResponse remoteGroupMemberResponse : remoteGroupMembers) {
				remoteGroupMemberResponse.setAccessType(
					loadAccessType(workspaceId, remoteGroupMemberResponse.getUuid()));
			}
			
			groupsResponse.add(buildRemoteGroupResponse(remoteGroup, remoteGroupMembers));
		}
		groupsResponse.add(makeEtcGroup(workspaceId, remoteGroups, workspaceMembers));
		return buildRemoteGroupListResponse(groupsResponse, workspaceMembers.size());
	}

	public RemoteGroupResponse getRemoteGroupDetail(
		String workspaceId,
		String groupId,
		String userId,
		String filter,
		String search,
		boolean includeOneself,
		boolean accessTypeFilter
	) {
		if (groupId.equals(ETC_GROUP_ID)) {
			List<RemoteGroup> remoteGroups = groupRepository.findByWorkspaceIdAndUserIdAndIncludeOneself(
				workspaceId, userId, includeOneself);
			List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(workspaceId);
			workspaceMembers = includeOneselfFilter(userId, includeOneself, workspaceMembers);
			return makeEtcGroup(workspaceId, remoteGroups, workspaceMembers);
		}

		RemoteGroup remoteGroup = groupRepository.findByWorkspaceIdAndGroupIdAndUserIdAndIncludeOneself(
				workspaceId, groupId, userId, includeOneself)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_GROUP_NOT_FOUND));

		List<WorkspaceMemberInfoResponse> workspaceMembers = workspaceRestService.getWorkspaceMembers(
			workspaceId, filter, search, 50).getData().getMemberInfoList();

		List<RemoteGroupMemberResponse> remoteGroupMembers = mapperWorkspaceToRemoteGroupMember(
			remoteGroup.getGroupMembers(), workspaceMembers);
		if (accessTypeFilter) {
			for (Iterator<RemoteGroupMemberResponse> groupMemberIterator = remoteGroupMembers.iterator(); groupMemberIterator.hasNext(); ) {
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId, groupMemberIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					groupMemberIterator.remove();
				}
			}
		}
		return buildRemoteGroupResponse(remoteGroup, remoteGroupMembers);
	}

	public RemoteGroupResponse updateRemoteGroup(
		String workspaceId,
		String userId,
		String groupId,
		GroupRequest groupRequest
	) {
		if (!isMasterOfWorkspace(workspaceId, userId)) {
			throw new RemoteServiceException(ErrorCode.ERR_ACCESS_AUTHORITY);
		}
		if (!isUserIdsIncludeOfWorkspace(workspaceId, groupRequest.getMemberList())) {
			throw new RemoteServiceException(ErrorCode.ERR_MEMBER_INVALID);
		}
		RemoteGroup remoteGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_GROUP_NOT_FOUND));

		if (!CollectionUtils.isEmpty(groupRequest.getMemberList())) {
			remoteGroup.getGroupMembers().clear();
			for (String uuid : groupRequest.getMemberList()) {
				remoteGroup.getGroupMembers().add(buildRemoteGroupMember(remoteGroup, uuid));
			}
		}
		remoteGroup.setGroupName(groupRequest.getGroupName());
		groupRepository.save(remoteGroup);

		List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(workspaceId);
		List<RemoteGroupMemberResponse> remoteGroupMembers = mapperWorkspaceToRemoteGroupMember(
			remoteGroup.getGroupMembers(), workspaceMembers);

		return buildRemoteGroupResponse(remoteGroup, remoteGroupMembers);
	}

	public ResultResponse deleteRemoteGroup(
		String workspaceId,
		String userId,
		String groupId
	) {
		if (!isMasterOfWorkspace(workspaceId, userId)) {
			throw new RemoteServiceException(ErrorCode.ERR_ACCESS_AUTHORITY);
		}

		RemoteGroup remoteGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_GROUP_NOT_FOUND));

		groupRepository.delete(remoteGroup);

		return ResultResponse.builder()
			.userId(userId)
			.result(true)
			.build();
	}

	public FavoriteGroupResponse createFavoriteGroup(
		String workspaceId,
		String userId,
		GroupRequest groupRequest
	) {
		if (!isUserIdsIncludeOfWorkspace(workspaceId, groupRequest.getMemberList())) {
			throw new RemoteServiceException(ErrorCode.ERR_MEMBER_INVALID);
		}
		if (favoriteGroupRepository.findByWorkspaceIdAndUserIdGroupCount(workspaceId, userId) >= GROUP_COUNT_LIMIT) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_COUNT_OVER);
		}
		if (groupRequest.getMemberList().size() > GROUP_MEMBER_COUNT_LIMIT) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_MEMBER_COUNT_OVER);
		}

		FavoriteGroup favoriteGroup = buildFavoriteGroup(workspaceId, groupRequest.getGroupName(), userId);
		for (String uuid : groupRequest.getMemberList()) {
			favoriteGroup.getFavoriteGroupMembers().add(buildFavoriteGroupMember(favoriteGroup, uuid));
		}

		favoriteGroupRepository.save(favoriteGroup);

		List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(
			workspaceId,
			favoriteGroup.getFavoriteGroupMembers()
				.stream()
				.map(FavoriteGroupMember::getUuid)
				.collect(Collectors.toList())
		);
		List<FavoriteGroupMemberResponse> favoriteGroupMembers = mapperWorkspaceToFavoriteMember(
			favoriteGroup.getFavoriteGroupMembers(), workspaceMembers);

		return buildFavoriteGroupResponse(favoriteGroup, favoriteGroupMembers);
	}

	public FavoriteGroupListResponse getFavoriteGroups(
		String workspaceId,
		String userId,
		boolean includeOneself
	) {
		List<FavoriteGroup> favoriteGroups = favoriteGroupRepository.findByWorkspaceIdAndUserIdAndIncludeOneself(
			workspaceId, userId, includeOneself);
		if (CollectionUtils.isEmpty(favoriteGroups)) {
			return FavoriteGroupListResponse.builder().favoriteGroupResponses(Collections.emptyList()).build();
		}

		// Make uuid array
		List<String> userIds = new ArrayList<>();
		for (FavoriteGroup favoriteGroup : favoriteGroups) {
			for (FavoriteGroupMember groupMember : favoriteGroup.getFavoriteGroupMembers()) {
				if (!(StringUtils.isBlank(groupMember.getUuid()))) {
					userIds.add(groupMember.getUuid());
				}
			}
		}
		HashSet<String> distinctData = new HashSet<>(userIds);
		List<String> removedDistinctUserIds = new ArrayList<>(distinctData);

		List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(workspaceId, removedDistinctUserIds);
		List<FavoriteGroupResponse> groupInfoResponseList = new ArrayList<>();
		for (FavoriteGroup favoriteGroup : favoriteGroups) {
			List<FavoriteGroupMemberResponse> favoriteGroupMembers = mapperWorkspaceToFavoriteMember(
				favoriteGroup.getFavoriteGroupMembers(), workspaceMembers);
			groupInfoResponseList.add(buildFavoriteGroupResponse(favoriteGroup, favoriteGroupMembers));
		}
		return FavoriteGroupListResponse.builder().favoriteGroupResponses(groupInfoResponseList).build();
	}

	public FavoriteGroupResponse getFavoriteGroupDetail(
		String workspaceId,
		String groupId,
		String userId,
		String filter,
		String search,
		boolean includeOneself,
		boolean accessTypeFilter
	) {
		FavoriteGroup favoriteGroup = favoriteGroupRepository.findByWorkspaceIdAndUserIdAndGroupIdAndIncludeOneself(
				workspaceId, groupId, userId, includeOneself)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_GROUP_NOT_FOUND));

		List<WorkspaceMemberInfoResponse> workspaceMembers = workspaceRestService.getWorkspaceMembers(
			workspaceId, filter, search, 50).getData().getMemberInfoList();
		workspaceMembers = includeOneselfFilter(userId, includeOneself, workspaceMembers);

		List<FavoriteGroupMemberResponse> favoriteGroupMembers = mapperWorkspaceToFavoriteMember(
			favoriteGroup.getFavoriteGroupMembers(),
			workspaceMembers
		);

		if (accessTypeFilter) {
			for (Iterator<FavoriteGroupMemberResponse> groupMemberIterator = favoriteGroupMembers.iterator(); groupMemberIterator.hasNext(); ) {
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId, groupMemberIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					groupMemberIterator.remove();
				}
			}
		}
		return buildFavoriteGroupResponse(favoriteGroup, favoriteGroupMembers);
	}

	public FavoriteGroupResponse updateFavoriteGroup(
		String workspaceId,
		String userId,
		String groupId,
		GroupRequest groupRequest
	) {
		if (!isUserIdsIncludeOfWorkspace(workspaceId, groupRequest.getMemberList())) {
			throw new RemoteServiceException(ErrorCode.ERR_MEMBER_INVALID);
		}

		if (groupRequest.getMemberList().size() > GROUP_MEMBER_COUNT_LIMIT) {
			throw new RemoteServiceException(ErrorCode.ERR_GROUP_MEMBER_COUNT_OVER);
		}

		FavoriteGroup favoriteGroup = favoriteGroupRepository.findByWorkspaceIdAndUserIdAndGroupId(
			workspaceId, userId, groupId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_GROUP_NOT_FOUND));

		if (!CollectionUtils.isEmpty(groupRequest.getMemberList())) {
			favoriteGroup.getFavoriteGroupMembers().clear();
			for (String uuid : groupRequest.getMemberList()) {
				favoriteGroup.getFavoriteGroupMembers().add(buildFavoriteGroupMember(favoriteGroup, uuid));
			}
		}
		favoriteGroup.setGroupName(groupRequest.getGroupName());

		favoriteGroupRepository.save(favoriteGroup);

		List<String> userIds = new ArrayList<>();
		for (FavoriteGroupMember groupMember : favoriteGroup.getFavoriteGroupMembers()) {
			if (!(StringUtils.isBlank(groupMember.getUuid()))) {
				userIds.add(groupMember.getUuid());
			}
		}

		List<WorkspaceMemberInfoResponse> workspaceMembers = getWorkspaceMembers(
			workspaceId,
			favoriteGroup.getFavoriteGroupMembers()
				.stream()
				.map(FavoriteGroupMember::getUuid)
				.collect(Collectors.toList())
		);
		List<FavoriteGroupMemberResponse> favoriteGroupMembers = mapperWorkspaceToFavoriteMember(
			favoriteGroup.getFavoriteGroupMembers(), workspaceMembers);
		return buildFavoriteGroupResponse(favoriteGroup, favoriteGroupMembers);
	}

	public ResultResponse deleteFavoriteGroup(
		String workspaceId,
		String userId,
		String groupId
	) {
		FavoriteGroup targetGroup = favoriteGroupRepository.findByWorkspaceIdAndUserIdAndGroupId(
			workspaceId, userId, groupId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_GROUP_NOT_FOUND));

		favoriteGroupRepository.delete(targetGroup);

		return ResultResponse.builder()
			.userId(userId)
			.result(true)
			.build();
	}

	private boolean isMasterOfWorkspace(String workspaceId, String userId) {
		WorkspaceMemberInfoResponse masterUserInfo = workspaceRestService.getWorkspaceMember(workspaceId, userId)
			.getData();
		return Role.MASTER == masterUserInfo.getRole();
	}

	private AccessType loadAccessType(String workspaceId, String uuid) {
		AccessType accessType;
		try {
			AccessStatus accessStatus = accessStatusService.getAccessStatus(workspaceId, uuid);
			if (ObjectUtils.isEmpty(accessStatus) || accessStatus.getAccessType() == AccessType.LOGOUT) {
				accessType = AccessType.LOGOUT;
			} else {
				accessType = accessStatus.getAccessType();
			}
		} catch (Exception e) {
			log.info("SET MEMBER STATUS EXCEPTION (uuid, exception message) => [{}], [{}]", uuid, e.getMessage());
			accessType = AccessType.LOGOUT;
		}
		return accessType;
	}

	private RemoteGroupResponse makeEtcGroup(
		String workspaceId,
		List<RemoteGroup> remoteGroups,
		List<WorkspaceMemberInfoResponse> workspaceMembers
	) {
		List<RemoteGroupMemberResponse> remoteGroupMembers = workspaceMembers.stream()
			.map(workspaceToRemoteGroupMemberMapper::toDto)
			.collect(Collectors.toList());

		// Make uuid set
		Set<String> userIds = new HashSet<>();
		for (RemoteGroup remoteGroup : remoteGroups) {
			userIds.addAll(
				remoteGroup.getGroupMembers().stream().map(RemoteGroupMember::getUuid).collect(Collectors.toSet()));
		}

		// Make Etc group
		Iterator<RemoteGroupMemberResponse> groupMemberIterator = remoteGroupMembers.listIterator();
		while (groupMemberIterator.hasNext()) {
			String memberUuid = groupMemberIterator.next().getUuid();
			for (String uuid : userIds) {
				if (memberUuid.equals(uuid)) {
					groupMemberIterator.remove();
				}
			}
		}

		for (RemoteGroupMemberResponse remoteGroupMemberResponse : remoteGroupMembers) {
			remoteGroupMemberResponse.setAccessType(loadAccessType(workspaceId, remoteGroupMemberResponse.getUuid()));
		}

		return buildRemoteGroupResponse(workspaceId, ETC_GROUP_ID, ETC_GROUP_NAME, remoteGroupMembers);
	}

	private List<FavoriteGroupMemberResponse> mapperWorkspaceToFavoriteMember(
		List<FavoriteGroupMember> favoriteGroupMembers,
		List<WorkspaceMemberInfoResponse> workspaceMembers
	) {
		List<FavoriteGroupMemberResponse> favoriteGroupMembersResponse = new ArrayList<>();
		for (FavoriteGroupMember favoriteGroupMember : favoriteGroupMembers) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : workspaceMembers) {
				if (favoriteGroupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					if (Arrays.asList(workspaceMemberInfoResponse.getLicenseProducts()).contains(RemoteLicense)) {
						FavoriteGroupMemberResponse favoriteGroupMemberResponse = workspaceToFavoriteGroupMemberMapper.toDto(
							workspaceMemberInfoResponse);
						favoriteGroupMemberResponse.setAccessType(
							loadAccessType(
								favoriteGroupMember.getFavoriteGroup().getWorkspaceId(),
								favoriteGroupMemberResponse.getUuid()
							)
						);
						favoriteGroupMembersResponse.add(favoriteGroupMemberResponse);
					} else {
						FavoriteGroupMember target = favoriteGroupMemberRepository.findByFavoriteGroupIdAndUuid(
							favoriteGroupMember.getFavoriteGroup().getId(),
							favoriteGroupMember.getUuid()
						).orElse(null);
						if (!ObjectUtils.isEmpty(target)) {
							target.setDeleted(true);
							favoriteGroupMemberRepository.save(target);
							log.info("REMOTE LICENSE NOT EXIST UUID -> {}", favoriteGroupMember.getUuid());
						}
					}
				}
			}
		}
		return favoriteGroupMembersResponse;
	}

	private List<RemoteGroupMemberResponse> mapperWorkspaceToRemoteGroupMember(
		List<RemoteGroupMember> remoteGroupMembers,
		List<WorkspaceMemberInfoResponse> workspaceMembers
	) {
		List<RemoteGroupMemberResponse> remoteGroupMemberResponses = new ArrayList<>();
		for (RemoteGroupMember remoteGroupMember : remoteGroupMembers) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : workspaceMembers) {
				if (remoteGroupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					if (Arrays.asList(workspaceMemberInfoResponse.getLicenseProducts()).contains(RemoteLicense)) {
						RemoteGroupMemberResponse remoteGroupMemberResponse = workspaceToRemoteGroupMemberMapper.toDto(
							workspaceMemberInfoResponse);
						remoteGroupMemberResponse.setAccessType(
							loadAccessType(
								remoteGroupMember.getRemoteGroup().getWorkspaceId(),
								remoteGroupMemberResponse.getUuid()
							)
						);
						remoteGroupMemberResponses.add(
							workspaceToRemoteGroupMemberMapper.toDto(workspaceMemberInfoResponse));
					} else {
						RemoteGroupMember target = remoteGroupMemberRepository.findByRemoteGroupIdAndUuid(
							remoteGroupMember.getRemoteGroup().getId(),
							remoteGroupMember.getUuid()
						).orElse(null);
						if (!ObjectUtils.isEmpty(target)) {
							target.setDeleted(true);
							remoteGroupMemberRepository.save(target);
							log.info("REMOTE LICENSE NOT EXIST UUID -> {}", remoteGroupMember.getUuid());
						}
					}
				}
			}
		}
		return remoteGroupMemberResponses;
	}

	private boolean isUserIdsIncludeOfWorkspace(String workspaceId, List<String> userIds) {
		Set<String> uuidSet = getWorkspaceMembers(workspaceId).stream()
			.map(WorkspaceMemberInfoResponse::getUuid)
			.collect(Collectors.toSet());
		for (String userId : userIds) {
			if (!uuidSet.contains(userId)) {
				return false;
			}
		}
		return true;
	}

	private RemoteGroup buildRemoteGroup(String workspaceId, String groupName) {
		return RemoteGroup.builder()
			.workspaceId(workspaceId)
			.groupId(makeGroupName())
			.groupName(groupName)
			.build();
	}

	private RemoteGroupMember buildRemoteGroupMember(RemoteGroup remoteGroup, String uuid) {
		return RemoteGroupMember.builder()
			.remoteGroup(remoteGroup)
			.uuid(uuid)
			.build();
	}

	private RemoteGroupResponse buildRemoteGroupResponse(
		RemoteGroup remoteGroup,
		List<RemoteGroupMemberResponse> remoteGroupMemberResponses
	) {
		return RemoteGroupResponse.builder()
			.workspaceId(remoteGroup.getWorkspaceId())
			.groupId(remoteGroup.getGroupId())
			.groupName(remoteGroup.getGroupName())
			.remoteGroupMemberResponses(remoteGroupMemberResponses)
			.memberCount(remoteGroupMemberResponses.size())
			.build();
	}

	private RemoteGroupResponse buildRemoteGroupResponse(
		String workspaceId,
		String groupId,
		String groupName,
		List<RemoteGroupMemberResponse> remoteGroupMemberResponses
	) {
		return RemoteGroupResponse.builder()
			.workspaceId(workspaceId)
			.groupId(groupId)
			.groupName(groupName)
			.remoteGroupMemberResponses(remoteGroupMemberResponses)
			.memberCount(remoteGroupMemberResponses.size())
			.build();
	}

	private RemoteGroupListResponse buildRemoteGroupListResponse(
		List<RemoteGroupResponse> groupInfoResponseList,
		long memberTotalCount
	) {
		return RemoteGroupListResponse.builder()
			.groupInfoResponseList(groupInfoResponseList)
			.memberTotalCount(memberTotalCount)
			.build();
	}

	private FavoriteGroupMember buildFavoriteGroupMember(FavoriteGroup favoriteGroup, String uuid) {
		return FavoriteGroupMember.builder()
			.favoriteGroup(favoriteGroup)
			.uuid(uuid)
			.build();
	}

	private FavoriteGroup buildFavoriteGroup(String workspaceId, String groupName, String uuid) {
		return FavoriteGroup.builder().workspaceId(workspaceId)
			.groupId(makeGroupName())
			.groupName(groupName)
			.uuid(uuid)
			.build();
	}

	private FavoriteGroupResponse buildFavoriteGroupResponse(
		FavoriteGroup favoriteGroup,
		List<FavoriteGroupMemberResponse> favoriteGroupMemberResponses
	) {
		return FavoriteGroupResponse.builder()
			.workspaceId(favoriteGroup.getWorkspaceId())
			.groupId(favoriteGroup.getGroupId())
			.groupName(favoriteGroup.getGroupName())
			.uuid(favoriteGroup.getUuid())
			.favoriteGroupMemberResponses(favoriteGroupMemberResponses)
			.build();
	}

	private List<WorkspaceMemberInfoResponse> includeOneselfFilter(
		String userId,
		boolean includeOneself,
		List<WorkspaceMemberInfoResponse> workspaceMembers
	) {
		if (!includeOneself) {
			return workspaceMembers.stream()
				.filter(workspaceMemberInfoResponse -> !workspaceMemberInfoResponse.getUuid().equals(userId))
				.collect(Collectors.toList());
		}
		return workspaceMembers;
	}

	private List<WorkspaceMemberInfoResponse> getWorkspaceMembers(String workspaceId) {
		return workspaceRestService.getWorkspaceMembers(workspaceId, "remote", 50).getData().getMemberInfoList();
	}

	private List<WorkspaceMemberInfoResponse> getWorkspaceMembers(String workspaceId, List<String> userIds) {
		return workspaceRestService.getWorkspaceMembersExcludeUserIds(
			workspaceId,
			userIds.toArray(new String[0])
		).getData().getMemberInfoList();
	}

	private String makeGroupName() {
		return GROUP + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphanumeric(9);
	}

}
