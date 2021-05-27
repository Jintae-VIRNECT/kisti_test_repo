package com.virnect.serviceserver.serviceremote.application;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.compress.utils.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseConstants;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberSecessionResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.application.workspace.WorkspaceRestService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final ModelMapper modelMapper;
	private final WorkspaceRestService workspaceRestService;
	private final RoomRepository roomRepository;
	private final MemberHistoryRepository memberHistoryRepository;

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
		int size
	) {

		WorkspaceMemberInfoListResponse responseData = workspaceRestService.getWorkspaceMembers(workspaceId).getData();
		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = responseData.getMemberInfoList();

		if (CollectionUtils.isEmpty(workspaceMemberInfoList)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
		}

		workspaceMemberInfoList.removeIf(memberInfoResponses ->
			Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty() ||
				!Arrays.toString(memberInfoResponses.getLicenseProducts())
					.contains(LicenseConstants.PRODUCT_NAME));

		workspaceMemberInfoList.removeIf(memberInfoResponses -> memberInfoResponses.getUuid().equals(userId));

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
			.map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
			.collect(Collectors.toList());

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
			.map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
			.collect(Collectors.toList());

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

}
