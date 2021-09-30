package com.virnect.serviceserver.serviceremote.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.global.util.paging.PagingUtils;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberHistoryMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.roomhistory.RoomHistoryDetailInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.roomhistory.RoomHistoryInfoMapper;
import com.virnect.data.dto.request.room.RoomHistoryDeleteRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.data.dto.response.room.RoomHistoryInfoListResponse;
import com.virnect.data.dto.response.room.RoomHistoryInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

	private final WorkspaceRestService workspaceRestService;
	private final RoomHistoryRepository roomHistoryRepository;
	private final MemberHistoryRepository memberHistoryRepository;

	private final RoomHistoryDetailInfoMapper roomHistoryDetailMapper;
	private final RoomHistoryInfoMapper roomHistoryInfoMapper;
	private final MemberHistoryMapper memberHistoryMapper;

	private List<MemberInfoResponse> setLeader(List<MemberInfoResponse> members) {
		members.sort((t1, t2) -> {
			int index1 = t1.getMemberType().ordinal();
			int index2 = t2.getMemberType().ordinal();
			return Integer.compare(index1, index2);
		});
		return members;
	}

	public RoomHistoryInfoListResponse getRoomHistoryCurrent(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {

		Page<RoomHistory> roomHistoryPage = roomHistoryRepository.findMyRoomHistorySpecificUserId(workspaceId, userId, paging, pageable);

		List<RoomHistoryInfoResponse> roomHistoryInfoResponses = makeRoomHistoryInfoResponses(workspaceId, roomHistoryPage);
		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			paging,
			pageable,
			roomHistoryPage.getNumberOfElements(),
			roomHistoryPage.getTotalPages(),
			roomHistoryPage.getTotalElements(),
			roomHistoryPage.isLast()
		);

		return new RoomHistoryInfoListResponse(roomHistoryInfoResponses, pageMeta);
	}

	public RoomHistoryInfoListResponse getHistoryListStandardSearch(
		String workspaceId,
		String userId,
		String search,
		Pageable pageable
	) {

		List<WorkspaceMemberInfoResponse> members = workspaceRestService.getWorkspaceMembers(
			workspaceId,
			"remote",
			search,
			99
		).getData().getMemberInfoList();

		List<String> userIds = new ArrayList<>();
		for (WorkspaceMemberInfoResponse memberInfo : members) {
			if (!StringUtils.isBlank(memberInfo.getUuid())){
				userIds.add(memberInfo.getUuid());
			}
		}

		Page<RoomHistory> roomHistoryPage = roomHistoryRepository.findMyRoomHistorySpecificUserIdBySearch(workspaceId, userId, userIds, search, pageable);

		List<RoomHistoryInfoResponse> roomHistoryInfoResponses = makeRoomHistoryInfoResponses(workspaceId, roomHistoryPage);
		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			true,
			pageable,
			roomHistoryPage.getNumberOfElements(),
			roomHistoryPage.getTotalPages(),
			roomHistoryPage.getTotalElements(),
			roomHistoryPage.isLast()
		);

		return new RoomHistoryInfoListResponse(roomHistoryInfoResponses, pageMeta);
	}

	public ApiResponse<RoomHistoryDetailInfoResponse> getHistoryBySessionId(
		String workspaceId,
		String sessionId
	) {
		LogMessage.formedInfo(
			//TAG,
			"invokeDataProcess",
			"loadRoomHistoryDetail",
			"room history detail info retrieve by session id",
			sessionId
		);

		RoomHistory roomHistory = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(roomHistory)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (MemberHistory member : roomHistory.getMemberHistories()) {
			if (!(Strings.isBlank(member.getUuid()))) {
				userList.add(member.getUuid());
			}
		}

		// Receive User list from Workspace
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMembersExcludeUserIds(
			workspaceId,
			userList.stream().distinct().toArray(String[]::new)
		);

		// mapping data
		RoomHistoryDetailInfoResponse resultResponse = roomHistoryDetailMapper.toDto(roomHistory);

		resultResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

		List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
			.map(memberHistoryMapper::toDto)
			.collect(Collectors.toList());

		RoomService.mapperWorkspaceMemberToMember(memberInfo, memberInfoList);
		resultResponse.setMemberList(setLeader(memberInfoList));

		return new ApiResponse<>(resultResponse);
	}

	public ApiResponse<ResultResponse> deleteHistory(
		String workspaceId,
		String userId
	) {
		LogMessage.formedInfo(
			//TAG,
			"invokeDataProcess",
			"removeRoomHistory",
			"room history info delete all by user id",
			userId
		);

		List<MemberHistory> memberHistories = memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);
		memberHistories.forEach(memberHistory -> {
			if (!ObjectUtils.isEmpty(memberHistory.getRoomHistory())) {
				memberHistory.setHistoryDeleted(true);
				this.memberHistoryRepository.save(memberHistory);
			}
		});

		ResultResponse resultResponse = new ResultResponse();
		resultResponse.userId = userId;
		resultResponse.setResult(true);

		return new ApiResponse<>(resultResponse);
	}

	public ApiResponse<ResultResponse> deleteHistoryById(
		String workspaceId,
		RoomHistoryDeleteRequest roomHistoryDeleteRequest
	) {
		LogMessage.formedInfo(
			//TAG,
			"invokeDataProcess",
			"removeRoomHistory",
			"some room history info delete by user id",
			roomHistoryDeleteRequest.getUuid()
		);

		for (String sessionId : roomHistoryDeleteRequest.getSessionIdList()) {

			MemberHistory memberHistory = memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(
				workspaceId, sessionId, roomHistoryDeleteRequest.getUuid()).orElse(null);

			if (!ObjectUtils.isEmpty(memberHistory)) {
				if (!ObjectUtils.isEmpty(memberHistory.getRoomHistory())) {
					memberHistory.setHistoryDeleted(true);
					this.memberHistoryRepository.save(memberHistory);
				}
			} else {
				//not send error polling
				LogMessage.formedInfo(
					//TAG,
					"invokeDataProcess",
					"removeRoomHistory",
					"room history info delete but member history data is null by session id",
					roomHistoryDeleteRequest.getUuid()
				);
			}
		}

		ResultResponse resultResponse = new ResultResponse();
		resultResponse.userId = roomHistoryDeleteRequest.getUuid();
		resultResponse.setResult(true);

		return new ApiResponse<>(resultResponse);
	}

	public RoomHistory getRoomHistory(String workspaceId, String sessionId) {
		return this.roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
	}

	private List<RoomHistoryInfoResponse> makeRoomHistoryInfoResponses(
			String workspaceId,
			Page<RoomHistory> roomHistoryPage
	) {
		List<RoomHistoryInfoResponse> roomHistoryInfoResponses = new ArrayList<>();
		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (RoomHistory roomHistory : roomHistoryPage) {
			for (MemberHistory memberHistory : roomHistory.getMemberHistories()) {
				if (memberHistory.getMemberType() == MemberType.LEADER && !(Strings.isBlank(memberHistory.getUuid()))) {
					userList.add(memberHistory.getUuid());
				}
			}
		}

		// Receive User list from Workspace
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMembersExcludeUserIds(
			workspaceId,
			userList.stream().distinct().toArray(String[]::new)
		);

		// Make Response data
		for (RoomHistory roomHistory : roomHistoryPage.getContent()) {
			RoomHistoryInfoResponse roomHistoryInfoResponse = roomHistoryInfoMapper.toDto(roomHistory);
			roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories()
				.stream()
				.map(memberHistoryMapper::toDto)
				.collect(Collectors.toList());

			// find and get extra information from use-server using uuid
			RoomService.mapperWorkspaceMemberToMember(memberInfo, memberInfoList);

			// Set Member List to Room Information Response
			roomHistoryInfoResponse.setMemberList(setLeader(memberInfoList));
			roomHistoryInfoResponses.add(roomHistoryInfoResponse);
		}
		return roomHistoryInfoResponses;
	}

}
