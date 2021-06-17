package com.virnect.serviceserver.serviceremote.application;

import java.time.LocalDateTime;
import java.util.Arrays;
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
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberWorkspaceMapper;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.data.dto.response.member.MemberInfoListResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.response.member.MemberSecessionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final WorkspaceRestService workspaceRestService;

	private final RoomRepository roomRepository;
	private final MemberHistoryRepository memberHistoryRepository;

	private final AccessStatusService accessStatusService;

	private final MemberWorkspaceMapper memberWorkspaceMapper;

	public WorkspaceMemberInfoListResponse getMembers(
		String workspaceId,
		String filter,
		String search,
		int page,
		int size
	) {

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId, filter, search, page, size).getData();

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();
		int currentPage = responseData.getPageMeta().getCurrentPage();
		int totalPage = responseData.getPageMeta().getTotalPage();

		// set Page Metadata
		responseData.getPageMeta().setNumberOfElements(workspaceMemberInfoList.size());
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

		//WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMembers(workspaceId).getData();

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, search, 0, Integer.MAX_VALUE).getData();

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();

		if (CollectionUtils.isEmpty(workspaceMemberInfoList)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
		}

		workspaceMemberInfoList.removeIf(memberInfoResponses ->
			Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
				!Arrays.toString(memberInfoResponses.getLicenseProducts())
					.contains(LicenseConstants.PRODUCT_NAME));

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

		int currentPage = page + 1; // current page number (start : 0)
		int pagingSize = size; // page data count
		long totalElements = workspaceMemberInfoList.size();
		int totalPage = totalElements % size == 0 ? (int)(totalElements / (size)) : (int)(totalElements / (size)) + 1;
		boolean last = (currentPage) == totalPage;

		int startIndex = 0;
		int endIndex = 0;
		if (!workspaceMemberInfoList.isEmpty()) {
			if (pagingSize > totalElements) {
				startIndex = 0;
				endIndex = (int)totalElements;
			} else {
				startIndex = (currentPage - 1) * pagingSize;
				endIndex = last ? workspaceMemberInfoList.size() : ((currentPage - 1) * pagingSize) + (pagingSize);
			}
		}

		// 데이터 range
		workspaceMemberInfoList = IntStream
			.range(startIndex, endIndex)
			.mapToObj(workspaceMemberInfoList::get)
			.collect(Collectors.toList());

		// 페이징 데이터 설정
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(currentPage)
			.currentSize(pagingSize)
			.numberOfElements(workspaceMemberInfoList.size())
			.totalPage(totalPage)
			.totalElements(totalElements)
			.last(last)
			.build();

		List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
			.map(workspaceMemberInfoResponse -> memberWorkspaceMapper.toDto(workspaceMemberInfoResponse))
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

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMembers(workspaceId).getData();
		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();

		workspaceMemberInfoList.removeIf(memberInfoResponses ->
			Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty()
				|| !Arrays.toString(memberInfoResponses.getLicenseProducts()).contains(LicenseConstants.PRODUCT_NAME)
		);

		List<WorkspaceMemberInfoResponse> finalWorkspaceMemberInfoList = workspaceMemberInfoList;
		room.getMembers().forEach(member -> {
			finalWorkspaceMemberInfoList.removeIf(memberInfoResponses ->
				member.getMemberStatus() != MemberStatus.EVICTED && memberInfoResponses.getUuid().equals(member.getUuid())
			);
		});
		workspaceMemberInfoList = finalWorkspaceMemberInfoList;

		List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
			.map(workspaceMemberInfoResponse -> memberWorkspaceMapper.toDto(workspaceMemberInfoResponse))
			.collect(Collectors.toList());

		/*for(Iterator<MemberInfoResponse> memberInfoResponseIterator = memberInfoList.iterator(); memberInfoResponseIterator.hasNext();){
			AccessStatus targetUser = accessStatusService.getAccessStatus(
				workspaceId + "_" + memberInfoResponseIterator.next().getUuid());
			if (ObjectUtils.isEmpty(targetUser)) {
				memberInfoResponseIterator.remove();
			} else {
				if (targetUser.getAccessType() == AccessType.LOGIN) {
					System.out.println("login user : " + targetUser.getId());
				} else {
					System.out.println("not login user : " + targetUser.getId() + ", status : " + targetUser.getAccessType());
					memberInfoResponseIterator.remove();
				}
			}
		}*/

		for (MemberInfoResponse memberInfoResponse : memberInfoList) {
			AccessStatus targetUser = accessStatusService.getAccessStatus(workspaceId + "_" + memberInfoResponse.getUuid());
			if (!ObjectUtils.isEmpty(targetUser)) {
				memberInfoResponse.setAccessType(targetUser.getAccessType());
			} else {
				memberInfoResponse.setAccessType(AccessType.LOGOUT);
			}
		}

		int currentPage = page + 1; // current page number (start : 0)
		int pagingSize = size; // page data count
		long totalElements = memberInfoList.size();
		int totalPage = totalElements % size == 0 ? (int)(totalElements / (size)) : (int)(totalElements / (size)) + 1;
		boolean last = (currentPage) == totalPage;

		int startIndex = 0;
		int endIndex = 0;

		if (!memberInfoList.isEmpty()) {
			if (pagingSize > totalElements) {
				startIndex = 0;
				endIndex = (int)totalElements;
			} else {
				startIndex = (currentPage - 1) * pagingSize;
				endIndex = last ? workspaceMemberInfoList.size() : ((currentPage - 1) * pagingSize) + (pagingSize);
			}
		}

		// 데이터 range
		memberInfoList = IntStream
			.range(startIndex, endIndex)
			.mapToObj(memberInfoList::get)
			.collect(Collectors.toList());

		// 페이징 데이터 설정
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(currentPage)
			.currentSize(pagingSize)
			.numberOfElements(memberInfoList.size())
			.totalPage(totalPage)
			.totalElements(totalElements)
			.last(last)
			.build();

		return new MemberInfoListResponse(memberInfoList, pageMeta);
	}

	public MemberSecessionResponse deleteMembersBySession(String userId) {

		List<MemberHistory> historyList = memberHistoryRepository.findAllByUuid(userId);
		for (MemberHistory memberHistory : historyList) {
			memberHistory.setMemberType(MemberType.SECESSION);
			memberHistoryRepository.save(memberHistory);
		}

		return new MemberSecessionResponse(userId, true, LocalDateTime.now());
	}

	public AccessType loadAccessType(String workspaceId, String uuid) {
		AccessType result = AccessType.LOGOUT;
		try {
			AccessStatus accessStatus = accessStatusService.getAccessStatus(workspaceId + "_" + uuid);
			if (ObjectUtils.isEmpty(accessStatus) || accessStatus.getAccessType() == AccessType.LOGOUT) {
				return AccessType.LOGOUT;
			}
			return accessStatus.getAccessType();
		} catch (Exception e) {
			log.info("SET MEMBER STATUS EXCEPTION => [{}]", uuid);
		}
		return result;
	}

}
