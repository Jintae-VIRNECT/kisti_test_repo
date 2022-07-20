package com.virnect.serviceserver.serviceremote.application;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.constraint.LicenseConstants;
import com.virnect.data.dto.response.member.MemberInfoListResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.response.member.MemberSecessionResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RemoteServiceException;
import com.virnect.data.global.util.paging.CustomPaging;
import com.virnect.data.global.util.paging.PagingUtils;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberWorkspaceMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final WorkspaceRestService workspaceRestService;
	private final AccessStatusService accessStatusService;

	private final RoomRepository roomRepository;
	private final MemberHistoryRepository memberHistoryRepository;

	private final MemberWorkspaceMapper memberWorkspaceMapper;

	public WorkspaceMemberInfoListResponse getMembers(
		String workspaceId,
		String filter,
		String search,
		int page,
		int size
	) {

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMembers(
			workspaceId, filter, search, page, size).getData();

		// set Page Metadata
		int currentPage = responseData.getPageMeta().getCurrentPage();
		int totalPage = responseData.getPageMeta().getTotalPage();
		responseData.getPageMeta().setNumberOfElements(responseData.getMemberInfoList().size());
		responseData.getPageMeta().setLast(currentPage >= totalPage);

		return responseData;
	}

	public MemberInfoListResponse getMembersExceptForMe(
		String workspaceId,
		String userId,
		String filter,
		String search,
		int page,
		int size,
		boolean accessTypeFilter
	) {

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = getAllMemberInWorkspace(workspaceId);
		if (CollectionUtils.isEmpty(workspaceMemberInfoList)) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
		}

		workspaceMemberInfoList.removeIf(
			memberInfoResponses ->
				Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
					!Arrays.toString(memberInfoResponses.getLicenseProducts())
						.contains(LicenseConstants.PRODUCT_NAME)
		);
		workspaceMemberInfoList.removeIf(memberInfoResponses -> memberInfoResponses.getUuid().equals(userId));

		if (accessTypeFilter) {
			for (Iterator<WorkspaceMemberInfoResponse> memberInfoIterator = workspaceMemberInfoList.iterator(); memberInfoIterator.hasNext(); ) {
				AccessStatus targetUser = accessStatusService.getAccessStatus(
					workspaceId, memberInfoIterator.next().getUuid());
				if (ObjectUtils.isEmpty(targetUser) || targetUser.getAccessType() != AccessType.LOGIN) {
					memberInfoIterator.remove();
				}
			}
		}

		CustomPaging customPaging = PagingUtils.customPaging(
			page, workspaceMemberInfoList.size(), size, workspaceMemberInfoList.isEmpty());

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

		return new MemberInfoListResponse(memberInfoList, pageMeta);
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
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = getAllMemberInWorkspace(workspaceId);

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
			AccessStatus targetUser = accessStatusService.getAccessStatus(workspaceId, memberInfoResponse.getUuid());
			if (!ObjectUtils.isEmpty(targetUser)) {
				memberInfoResponse.setAccessType(targetUser.getAccessType());
			} else {
				memberInfoResponse.setAccessType(AccessType.LOGOUT);
			}
		}

		CustomPaging customPaging = PagingUtils.customPaging(
			page, workspaceMemberInfoList.size(), size, workspaceMemberInfoList.isEmpty());

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

	public List<WorkspaceMemberInfoResponse> getAllMemberInWorkspace(
		String workspaceId
	) {
		WorkspaceMemberInfoListResponse firstPageAndMetaData = workspaceRestService.getWorkspaceMembers(
			workspaceId, "email,desc", 1, Integer.MAX_VALUE).getData();
		int totalPage = firstPageAndMetaData.getPageMeta().getTotalPage();

		List<WorkspaceMemberInfoResponse> allMemberList = firstPageAndMetaData.getMemberInfoList();

		for (int page = 2; page <= totalPage; page++) {
			List<WorkspaceMemberInfoResponse> response = workspaceRestService.getWorkspaceMembers(
				workspaceId, "email,desc", page, Integer.MAX_VALUE).getData().getMemberInfoList();
			allMemberList.addAll(response);
		}
		log.info("[getAllMemberInWorkspace] totalPage : [{}], allMemberListSize : [{}]",
			totalPage, allMemberList.size()
		);

		return allMemberList.stream().sorted(Comparator.comparing(WorkspaceMemberInfoResponse::getName))
			.collect(Collectors.toList());
	}

	public MemberSecessionResponse deleteMembersBySession(String userId) {
		for (MemberHistory memberHistory : memberHistoryRepository.findAllByUuid(userId)) {
			memberHistory.setMemberType(MemberType.SECESSION);
			memberHistoryRepository.save(memberHistory);
		}
		return new MemberSecessionResponse(userId, true, LocalDateTime.now());
	}

	private AccessType loadAccessType(String workspaceId, String uuid) {
		AccessType result;
		try {
			AccessStatus accessStatus = accessStatusService.getAccessStatus(workspaceId, uuid);
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

}
