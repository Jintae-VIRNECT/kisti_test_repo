package com.virnect.serviceserver.serviceremote.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.virnect.data.dao.member.RemoteGroupMemberRepository;
import com.virnect.data.domain.member.MemberAuthType;
import com.virnect.data.dto.response.member.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.member.RemoteGroupRepository;
import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.member.RemoteGroup;
import com.virnect.data.domain.member.RemoteGroupMember;
import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.constraint.LicenseConstants;
import com.virnect.data.dto.request.member.GroupRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.global.util.paging.CustomPaging;
import com.virnect.data.global.util.paging.PagingUtils;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberWorkspaceMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.RemoteGroupMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.RemoteGroupMemberMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final WorkspaceRestService workspaceRestService;
	private final AccessStatusService accessStatusService;

	private final RoomRepository roomRepository;
	private final MemberHistoryRepository memberHistoryRepository;
	private final RemoteGroupRepository groupRepository;
	private final RemoteGroupMemberRepository remoteGroupMemberRepository;

	private final MemberWorkspaceMapper memberWorkspaceMapper;
	private final RemoteGroupMemberMapper remoteGroupMemberMapper;
	private final RemoteGroupMapper remoteGroupMapper;

	public WorkspaceMemberInfoListResponse getMembers(
		String workspaceId,
		String filter,
		String search,
		int page,
		int size
	) {

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, page, size).getData();

		// set Page Metadata
		int currentPage = responseData.getPageMeta().getCurrentPage();
		int totalPage = responseData.getPageMeta().getTotalPage();
		responseData.getPageMeta().setNumberOfElements(responseData.getMemberInfoList().size());
		responseData.getPageMeta().setLast(currentPage >= totalPage);

		return responseData;
	}

	public ApiResponse<MemberInfoListResponse> getMembersExceptForMe(
		String workspaceId,
		String userId,
		String filter,
		String search,
		int page,
		int size,
		boolean accessTypeFilter
	) {

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, 0, Integer.MAX_VALUE).getData();

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();
		if (CollectionUtils.isEmpty(workspaceMemberInfoList)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
		}

		workspaceMemberInfoList.removeIf(
			memberInfoResponses ->
				Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
				!Arrays.toString(memberInfoResponses.getLicenseProducts())
					.contains(LicenseConstants.PRODUCT_NAME)
		);
		workspaceMemberInfoList.removeIf(memberInfoResponses -> memberInfoResponses.getUuid().equals(userId));

		if (accessTypeFilter) {
			for(Iterator<WorkspaceMemberInfoResponse> memberInfoIterator = workspaceMemberInfoList.iterator(); memberInfoIterator.hasNext();){
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId + "_" + memberInfoIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					memberInfoIterator.remove();
				}
			}
		}

		CustomPaging customPaging = PagingUtils.customPaging(page, workspaceMemberInfoList.size(), size, workspaceMemberInfoList.isEmpty());

		// 데이터 range
		workspaceMemberInfoList = IntStream
			.range(customPaging.getStartIndex(), customPaging.getEndIndex())
			.mapToObj(workspaceMemberInfoList::get)
			.collect(Collectors.toList());

		// 페이징 데이터 설정
		customPaging.setNumberOfElements(workspaceMemberInfoList.size());
		PageMetadataResponse pageMeta = PagingUtils.customPagingBuilder(customPaging);

		List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
			.map(memberWorkspaceMapper::toDto)
			.collect(Collectors.toList());

		// Redis 내 멤버 접속상태 확인
		for (MemberInfoResponse memberInfoResponse : memberInfoList) {
			memberInfoResponse.setAccessType(loadAccessType(workspaceId, memberInfoResponse.getUuid()));
		}

		return new ApiResponse<>(new MemberInfoListResponse(memberInfoList,pageMeta));
	}
  
	public MemberInfoListResponse getMembersInvitePossible(
		String workspaceId,
		String sessionId,
		String userId,
		String filter,
		String search,
		int page,
		int size
	) {

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMembers(workspaceId).getData();
		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();

		workspaceMemberInfoList.removeIf(
			memberInfoResponses -> Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty()
				|| !Arrays.toString(memberInfoResponses.getLicenseProducts()).contains(LicenseConstants.PRODUCT_NAME)
		);

		List<WorkspaceMemberInfoResponse> finalWorkspaceMemberInfoList = workspaceMemberInfoList;
		room.getMembers().forEach(member -> finalWorkspaceMemberInfoList.removeIf(memberInfoResponses ->
			member.getMemberStatus() != MemberStatus.EVICTED && memberInfoResponses.getUuid().equals(member.getUuid())

		));
		workspaceMemberInfoList = finalWorkspaceMemberInfoList;

		List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
			.map(memberWorkspaceMapper::toDto)
			.collect(Collectors.toList());

		for (MemberInfoResponse memberInfoResponse : memberInfoList) {
			AccessStatus targetUser = accessStatusService.getAccessStatus(workspaceId + "_" + memberInfoResponse.getUuid());
			if (!ObjectUtils.isEmpty(targetUser)) {
				memberInfoResponse.setAccessType(targetUser.getAccessType());
			} else {
				memberInfoResponse.setAccessType(AccessType.LOGOUT);
			}
		}

		CustomPaging customPaging = PagingUtils.customPaging(page, workspaceMemberInfoList.size(), size, workspaceMemberInfoList.isEmpty());

		// 데이터 range
		memberInfoList = IntStream
			.range(customPaging.getStartIndex(), customPaging.getEndIndex())
			.mapToObj(memberInfoList::get)
			.collect(Collectors.toList());

		// 페이징 데이터 설정
		customPaging.setNumberOfElements(workspaceMemberInfoList.size());
		PageMetadataResponse pageMeta = PagingUtils.customPagingBuilder(customPaging);

		return new MemberInfoListResponse(memberInfoList, pageMeta);
	}

	public MemberSecessionResponse deleteMembersBySession(String userId) {
		for (MemberHistory memberHistory : memberHistoryRepository.findAllByUuid(userId)) {
			memberHistory.setMemberType(MemberType.SECESSION);
			memberHistoryRepository.save(memberHistory);
		}
		return new MemberSecessionResponse(userId, true, LocalDateTime.now());
	}

	public AccessType loadAccessType(String workspaceId, String uuid) {
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

	public ApiResponse<RemoteGroupInfoResponse> createGroup(
		String workspaceId,
		String userId,
		GroupRequest groupRequest,
		MemberAuthType memberAuthType
	) {
		// 권한 확인 (Only Master)
		if (memberAuthType == MemberAuthType.MASTER) {
			if (!checkMaster(workspaceId, userId)) {
				return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
			}
		}

		long groupCount = groupRepository.findByWorkspaceIdAndUserIdGroupCount(workspaceId, userId);
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
			.uuid(userId)
			.build();

		for (String uuid : groupRequest.getMemberList()) {
			RemoteGroupMember groupMember = RemoteGroupMember.builder()
				.remoteGroup(remoteGroup)
				.workspaceId(workspaceId)
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

	public ApiResponse<RemoteGroupInfoListResponse> getGroups(
		String workspaceId,
		String userId
	) {

		// 본인이 생성한 그룹 가져오기
		List<RemoteGroup> groups = groupRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
		if (CollectionUtils.isEmpty(groups)) {
			return new ApiResponse<>(
					RemoteGroupInfoListResponse.builder().groupInfoResponseList(Collections.emptyList()).build()
			);
		}

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (RemoteGroup remoteGroup : groups) {
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

		List<RemoteGroupInfoResponse> groupInfoResponseList = new ArrayList<>();
		for (RemoteGroup remoteGroup : groups) {
			RemoteGroupInfoResponse remoteGroupInfoResponse = remoteGroupMapper.toDto(remoteGroup);
			// Mapping Member List Data to Member Information List
			List<RemoteGroupMemberInfoResponse> groupMembersInfo = remoteGroup.getGroupMembers().stream()
				.map(remoteGroupMemberMapper::toDto)
				.collect(Collectors.toList());
			// Set nick name, profile in workspace members info
			for (RemoteGroupMemberInfoResponse groupMember : groupMembersInfo) {
				for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
					if (groupMember.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
						groupMember.setNickName(workspaceMemberInfoResponse.getNickName());
						groupMember.setProfile(workspaceMemberInfoResponse.getProfile());
					}
				}
			}

			remoteGroupInfoResponse.setRemoteGroupMemberInfoResponseList(groupMembersInfo);
			groupInfoResponseList.add(remoteGroupInfoResponse);
		}
		
		return new ApiResponse<>(RemoteGroupInfoListResponse.builder().groupInfoResponseList(groupInfoResponseList).build());
	}

	public ApiResponse<RemoteGroupInfoResponse> getGroup(
		String workspaceId,
		String groupId,
		String filter,
		String search,
		boolean accessTypeFilter
	) {

		RemoteGroup remoteGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId);
		if (ObjectUtils.isEmpty(remoteGroup)) {
			return new ApiResponse<>(new RemoteGroupInfoResponse());
		}

		WorkspaceMemberInfoListResponse memberInfo = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, 0, Integer.MAX_VALUE).getData();

		RemoteGroupInfoResponse remoteGroupInfoResponse = remoteGroupMapper.toDto(remoteGroup);
		// Mapping Member List Data to Member Information List
		List<RemoteGroupMemberInfoResponse> groupMembersInfo = remoteGroup.getGroupMembers().stream()
			.map(remoteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (RemoteGroupMemberInfoResponse groupMember : groupMembersInfo) {
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
			for(Iterator<RemoteGroupMemberInfoResponse> groupMemberIterator = groupMembersInfo.iterator(); groupMemberIterator.hasNext();){
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId + "_" + groupMemberIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					groupMemberIterator.remove();
				}
			}
		}

		remoteGroupInfoResponse.setRemoteGroupMemberInfoResponseList(groupMembersInfo);
		return new ApiResponse<>(remoteGroupInfoResponse);
	}

	public ApiResponse<RemoteGroupInfoResponse> updateGroup(
		String workspaceId,
		String userId,
		String groupId,
		GroupRequest groupRequest,
		MemberAuthType memberAuthType
	) {

		RemoteGroup targetGroup;
		// 권한 확인 (Only Master)
		if (memberAuthType == MemberAuthType.MASTER) {
			if (!checkMaster(workspaceId, userId)) {
				return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
			}
			targetGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId);
		} else {
			targetGroup = groupRepository.findByWorkspaceIdAndGroupIdAndUserId(workspaceId, groupId, userId);
		}

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
					.workspaceId(workspaceId)
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

	public ApiResponse<ResultResponse> deleteGroup(
		String workspaceId,
		String userId,
		String groupId,
		MemberAuthType memberAuthType
	) {

		RemoteGroup targetGroup;

		// 권한 확인 (Only Master)
		if (memberAuthType == MemberAuthType.MASTER) {
			if (!checkMaster(workspaceId, userId)) {
				return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
			}
			targetGroup = groupRepository.findByWorkspaceIdAndGroupId(workspaceId, groupId);
		} else {
			targetGroup = groupRepository.findByWorkspaceIdAndGroupIdAndUserId(workspaceId, groupId, userId);
		}

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

	private RemoteGroupInfoResponse setGroupMembersInfo(RemoteGroup remoteGroup) {

		RemoteGroupInfoResponse remoteGroupInfoResponse = remoteGroupMapper.toDto(remoteGroup);
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
		List<RemoteGroupMemberInfoResponse> groupMembersInfo = remoteGroup.getGroupMembers().stream()
			.map(remoteGroupMemberMapper::toDto)
			.collect(Collectors.toList());
		// Set nick name, profile in workspace members info
		for (RemoteGroupMemberInfoResponse remoteGroupMemberInfoResponse : groupMembersInfo) {
			for (WorkspaceMemberInfoResponse workspaceMemberInfoResponse : memberInfo.getData().getMemberInfoList()) {
				if (remoteGroupMemberInfoResponse.getUuid().equals(workspaceMemberInfoResponse.getUuid())) {
					remoteGroupMemberInfoResponse.setNickName(workspaceMemberInfoResponse.getNickName());
					remoteGroupMemberInfoResponse.setProfile(workspaceMemberInfoResponse.getProfile());
				}
			}
		}
		remoteGroupInfoResponse.setRemoteGroupMemberInfoResponseList(groupMembersInfo);

		return remoteGroupInfoResponse;
	}

}
