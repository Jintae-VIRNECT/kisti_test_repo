package com.virnect.serviceserver.serviceremote.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.memberhistory.MemberHistoryRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.dto.request.room.RoomHistoryDeleteRequest;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomDetailInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomHistoryInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomHistoryInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

	private final WorkspaceRestService workspaceRestService;
	private final RoomHistoryRepository roomHistoryRepository;
	private final MemberHistoryRepository memberHistoryRepository;
	private final ModelMapper modelMapper;

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
		// Response data
		List<RoomHistoryInfoResponse> roomHistoryInfoResponses = new ArrayList<>();
		PageMetadataResponse pageMeta;

		// Receive RoomHistory list page from DB
		Page<RoomHistory> roomHistoryPage = roomHistoryRepository.findMyRoomHistorySpecificUserId(workspaceId, userId, paging, pageable);

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (RoomHistory roomHistory : roomHistoryPage) {
			for (MemberHistory memberHistory : roomHistory.getMemberHistories()) {
				if (memberHistory.getMemberType() == MemberType.LEADER && !(memberHistory.getUuid() == null || memberHistory.getUuid().isEmpty())) {
					userList.add(memberHistory.getUuid());
				}
			}
		}
		String[] userIds = userList.stream().distinct().toArray(String[]::new);

		// Receive User list from Workspace
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(workspaceId, userIds);

		// Make Response data
		for (RoomHistory roomHistory : roomHistoryPage.getContent()) {
			RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
			roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// find and get extra information from use-server using uuid
			for (MemberInfoResponse memberInfoResponse : memberInfoList) {
				for (WorkspaceMemberInfoResponse workspaceMemberInfo : memberInfo.getData().getMemberInfoList()) {
					if (memberInfoResponse.getUuid().equals(workspaceMemberInfo.getUuid())) {
						memberInfoResponse.setRole(workspaceMemberInfo.getRole());
						memberInfoResponse.setEmail(workspaceMemberInfo.getEmail());
						memberInfoResponse.setName(workspaceMemberInfo.getName());
						memberInfoResponse.setNickName(workspaceMemberInfo.getNickName());
						memberInfoResponse.setProfile(workspaceMemberInfo.getProfile());
					}
				}
			}

			// Set Member List to Room Information Response
			roomHistoryInfoResponse.setMemberList(setLeader(memberInfoList));
			roomHistoryInfoResponses.add(roomHistoryInfoResponse);
		}

		if (paging) {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.numberOfElements(roomHistoryPage.getNumberOfElements())
				.totalPage(roomHistoryPage.getTotalPages())
				.totalElements(roomHistoryPage.getTotalElements())
				.last(roomHistoryPage.isLast())
				.build();
		} else {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(0)
				.currentSize(0)
				.numberOfElements(roomHistoryPage.getNumberOfElements())
				.totalPage(1)
				.totalElements(roomHistoryPage.getTotalElements())
				.last(true)
				.build();
		}
		return new RoomHistoryInfoListResponse(roomHistoryInfoResponses, pageMeta);
	}

	public RoomHistoryInfoListResponse getHistoryListStandardSearch(
		String workspaceId,
		String userId,
		String search,
		Pageable pageable
	) {
		// Response data
		List<RoomHistoryInfoResponse> roomHistoryInfoResponses = new ArrayList<>();
		PageMetadataResponse pageMeta;

		Page<RoomHistory> roomHistoryPage;
		if (!(StringUtils.isBlank(search))) {
			List<String> userIds = new ArrayList<>();
			List<WorkspaceMemberInfoResponse> members = workspaceRestService.getWorkspaceMemberInfoList(
				workspaceId,
				"remote",
				search,
				99
			).getData().getMemberInfoList();

			for (WorkspaceMemberInfoResponse memberInfo : members) {
				if (memberInfo.getUuid() == null || memberInfo.getUuid().isEmpty()) {
					//if memberInfo is empty
					log.info("loadFromDatabase::searchRoomHistoryPageList:: some member dose not have uuid");
				} else {
					userIds.add(memberInfo.getUuid());
					log.info("userIds : ", memberInfo.getUuid());
				}
			}
			roomHistoryPage = roomHistoryRepository.findMyRoomHistorySpecificUserIdBySearch(workspaceId, userId, userIds, search, pageable);
		} else {
			roomHistoryPage = roomHistoryRepository.findMyRoomHistorySpecificUserId(workspaceId, userId, true, pageable);
		}

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (RoomHistory roomHistory : roomHistoryPage) {
			for (MemberHistory memberHistory : roomHistory.getMemberHistories()) {
				if (memberHistory.getMemberType() == MemberType.LEADER && !(memberHistory.getUuid() == null || memberHistory.getUuid().isEmpty())) {
					userList.add(memberHistory.getUuid());
				}
			}
		}

		// Receive User list from Workspace
		String[] userIds = userList.stream().distinct().toArray(String[]::new);
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(workspaceId, userIds);

		// Make Response data
		for (RoomHistory roomHistory : roomHistoryPage.getContent()) {
			RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
			roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// find and get extra information from use-server using uuid
			for (MemberInfoResponse memberInfoResponse : memberInfoList) {
				for (WorkspaceMemberInfoResponse workspaceMemberInfo : memberInfo.getData().getMemberInfoList()) {
					if (memberInfoResponse.getUuid().equals(workspaceMemberInfo.getUuid())) {
						memberInfoResponse.setRole(workspaceMemberInfo.getRole());
						memberInfoResponse.setEmail(workspaceMemberInfo.getEmail());
						memberInfoResponse.setName(workspaceMemberInfo.getName());
						memberInfoResponse.setNickName(workspaceMemberInfo.getNickName());
						memberInfoResponse.setProfile(workspaceMemberInfo.getProfile());
					}
				}
			}

			// Set Member List to Room Information Response
			roomHistoryInfoResponse.setMemberList(setLeader(memberInfoList));
			roomHistoryInfoResponses.add(roomHistoryInfoResponse);
		}

		pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(roomHistoryPage.getNumberOfElements())
			.totalPage(roomHistoryPage.getTotalPages())
			.totalElements(roomHistoryPage.getTotalElements())
			.last(roomHistoryPage.isLast())
			.build();

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

		// Response data
		ApiResponse<RoomHistoryDetailInfoResponse> responseData;

		RoomHistory roomHistory = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);

		if (roomHistory == null) {
			RoomHistoryDetailInfoResponse empty = new RoomHistoryDetailInfoResponse();
			responseData = new ApiResponse<>(empty, ErrorCode.ERR_ROOM_NOT_FOUND);
		} else {

			// Make uuid array
			List<String> userList = new ArrayList<>();
			for (MemberHistory member : roomHistory.getMemberHistories()) {
				if (!(member.getUuid() == null || member.getUuid().isEmpty())) {
					userList.add(member.getUuid());
				}
			}
			String[] userIds = userList.stream().distinct().toArray(String[]::new);

			// Receive User list from Workspace
			ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(workspaceId, userIds);

			// mapping data
			RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);
			resultResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			for (MemberInfoResponse memberInfoResponse : memberInfoList) {
				for (WorkspaceMemberInfoResponse workspaceMemberInfo : memberInfo.getData().getMemberInfoList()) {
					if (memberInfoResponse.getUuid().equals(workspaceMemberInfo.getUuid())) {
						memberInfoResponse.setRole(workspaceMemberInfo.getRole());
						memberInfoResponse.setEmail(workspaceMemberInfo.getEmail());
						memberInfoResponse.setName(workspaceMemberInfo.getName());
						memberInfoResponse.setNickName(workspaceMemberInfo.getNickName());
						memberInfoResponse.setProfile(workspaceMemberInfo.getProfile());
					}
				}
			}

			resultResponse.setMemberList(setLeader(memberInfoList));
			responseData = new ApiResponse<>(resultResponse);
		}
		return responseData;
	}

	public ApiResponse<ResultResponse> deleteHistory(
		String workspaceId,
		String userId
	) {

		ApiResponse<ResultResponse> responseDate;

		List<MemberHistory> memberHistories = memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);

		LogMessage.formedInfo(
			//TAG,
			"invokeDataProcess",
			"removeRoomHistory",
			"room history info delete all by user id",
			userId
		);

		memberHistories.forEach(memberHistory -> {
			if (memberHistory.getRoomHistory() != null) {
				//memberHistory.setRoomHistory(null);
				memberHistory.setHistoryDeleted(true);
				this.memberHistoryRepository.save(memberHistory);
			}
		});

		ResultResponse resultResponse = new ResultResponse();
		resultResponse.userId = userId;
		resultResponse.setResult(true);

		responseDate = new ApiResponse<>(resultResponse);

		return responseDate;
	}

	public ApiResponse<ResultResponse> deleteHistoryById(
		String workspaceId
		, RoomHistoryDeleteRequest roomHistoryDeleteRequest
	) {

		ApiResponse<ResultResponse> responseDate;

		LogMessage.formedInfo(
			//TAG,
			"invokeDataProcess",
			"removeRoomHistory",
			"some room history info delete by user id",
			roomHistoryDeleteRequest.getUuid()
		);

		for (String sessionId : roomHistoryDeleteRequest.getSessionIdList()) {
			String userId = roomHistoryDeleteRequest.getUuid();
			MemberHistory memberHistory = memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(
				workspaceId, sessionId, userId
			).orElse(null);
			if (memberHistory != null) {
				if (memberHistory.getRoomHistory() != null) {
					//memberHistory.setRoomHistory(null);

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
		responseDate = new ApiResponse<>(resultResponse);

		return responseDate;
	}

	public RoomHistory getRoomHistory(String workspaceId, String sessionId) {
		return this.roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
	}

}
