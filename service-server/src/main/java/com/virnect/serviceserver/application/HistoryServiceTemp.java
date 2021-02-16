package com.virnect.serviceserver.application;

import static com.virnect.data.dao.HistorySpecifications.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.RoomHistory;
import com.virnect.data.dao.MemberHistoryRepository;
import com.virnect.data.dao.RoomHistoryRepository;
import com.virnect.remote.application.workspace.WorkspaceRestService;
import com.virnect.serviceserver.dao.HistoryDataRepository;
import com.virnect.data.dto.request.room.RoomHistoryDeleteRequest;
import com.virnect.data.dto.response.PageMetadataResponse;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.data.dto.response.room.RoomHistoryInfoListResponse;
import com.virnect.data.dto.response.room.RoomHistoryInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceTemp {

	private static final String TAG = HistoryDataRepository.class.getSimpleName();

	private final WorkspaceRestService workspaceRestService;
	private final RoomHistoryRepository roomHistoryRepository;
	private final MemberHistoryRepository memberHistoryRepository;
	private final ModelMapper modelMapper;

	public RoomHistoryInfoListResponse getRoomHistoryCurrent(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {

		Page<MemberHistory> roomHistories;
		PageMetadataResponse pageMeta;

		roomHistories = memberHistoryRepository.findByWorkspaceIdAndUuidAndRoomHistoryIsNotNullAndHistoryDeletedFalse(workspaceId, userId, pageable);

		List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
		for (MemberHistory memberHistory : roomHistories.getContent()) {
			RoomHistory roomHistory = memberHistory.getRoomHistory();
			RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(
				roomHistory, RoomHistoryInfoResponse.class);
			roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
				.filter(member -> !member.getMemberType().equals(MemberType.SECESSION))
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// find and get extra information from use-server using uuid
			for (MemberInfoResponse memberInfoResponse : memberInfoList) {
				if (memberInfoResponse.getMemberType().equals(MemberType.LEADER)) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
						workspaceId, memberInfoResponse.getUuid());
					//log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());

					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					//memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}
			}

			memberInfoList.sort((t1, t2) -> {
				if (t1.getMemberType().equals(MemberType.LEADER)) {
					return 1;
				}
				return 0;
			});
			// Set Member List to Room Information Response
			roomHistoryInfoResponse.setMemberList(memberInfoList);
			roomHistoryInfoList.add(roomHistoryInfoResponse);
		}

		if (paging) {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.numberOfElements(roomHistories.getNumberOfElements())
				.totalPage(roomHistories.getTotalPages())
				.totalElements(roomHistories.getTotalElements())
				.last(roomHistories.isLast())
				.build();
		} else {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(0)
				.currentSize(0)
				.numberOfElements(roomHistories.getNumberOfElements())
				.totalPage(roomHistories.getTotalPages())
				.totalElements(roomHistories.getTotalElements())
				.last(roomHistories.isLast())
				.build();
		}

		return new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta);
	}

	public RoomHistoryInfoListResponse getHistoryListStandardSearch(
		String workspaceId,
		String userId,
		String search,
		Pageable pageable
	) {

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId,
			"remote",
			search,
			pageable.getPageNumber(),
			pageable.getPageSize()
		).getData().getMemberInfoList();

		List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
			.map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
			.collect(Collectors.toList());

		for (MemberInfoResponse memberInfoResponse : memberInfoList) {
			log.info("fetchFromRepository::searchRoomHistoryPageList:: {}", memberInfoResponse.toString());
		}

		Page<RoomHistory> roomHistories;
		List<String> userIds = new ArrayList<>();
		for (MemberInfoResponse memberInfo : memberInfoList) {
			if (memberInfo.getUuid() == null || memberInfo.getUuid().isEmpty()) {
				//if memberInfo is empty
				log.info("loadFromDatabase::searchRoomHistoryPageList:: some member dose not have uuid");
			} else {
				userIds.add(memberInfo.getUuid());
			}
		}

		if (userIds.isEmpty()) {
			log.info(
				"loadFromDatabase::searchRoomHistoryPageList::memberInfoList is empty can not find, search with room title");
			roomHistories = roomHistoryRepository.findAll(joinMemberHistory(workspaceId, userId, search), pageable);
		} else {
			log.info("loadFromDatabase::searchRoomHistoryPageList::memberInfoList is not empty");
			roomHistories = roomHistoryRepository.findAll(joinMemberHistory(workspaceId, userId, userIds, search), pageable);
		}

		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(roomHistories.getNumberOfElements())
			.totalPage(roomHistories.getTotalPages())
			.totalElements(roomHistories.getTotalElements())
			.last(roomHistories.isLast())
			.build();

		List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
		for (RoomHistory roomHistory : roomHistories.getContent()) {
			RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(
				roomHistory, RoomHistoryInfoResponse.class);
			roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			memberInfoList = roomHistory.getMemberHistories().stream()
				.filter(member -> !member.getMemberType().equals(MemberType.SECESSION))
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// find and get extra information from use-server using uuid
			for (MemberInfoResponse memberInfoResponse : memberInfoList) {
				if (memberInfoResponse.getMemberType().equals(MemberType.LEADER)) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
						workspaceId, memberInfoResponse.getUuid());
					log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}
			}

			memberInfoList.sort((t1, t2) -> {
				if (t1.getMemberType().equals(MemberType.LEADER)) {
					return 1;
				}
				return 0;
			});
			// Set Member List to Room Information Response
			roomHistoryInfoResponse.setMemberList(memberInfoList);

			roomHistoryInfoList.add(roomHistoryInfoResponse);
		}
		return new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta);
	}

	public ApiResponse<RoomHistoryDetailInfoResponse> getHistoryBySessionId(
		String workspaceId,
		String sessionId
	) {
		ApiResponse<RoomHistoryDetailInfoResponse> responseData;

		List<MemberInfoResponse> memberInfoList;
		RoomHistory roomHistory = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);

		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"loadRoomHistoryDetail",
			"room history detail info retrieve by session id",
			sessionId
		);

		if (roomHistory == null) {
			RoomHistoryDetailInfoResponse empty = new RoomHistoryDetailInfoResponse();
			responseData = new ApiResponse(empty, ErrorCode.ERR_ROOM_NOT_FOUND);
		} else {
			// mapping data
			RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(
				roomHistory, RoomHistoryDetailInfoResponse.class);
			resultResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

			// Get Member List by Room Session ID
			// Mapping Member List Data to Member Information List
			memberInfoList = memberHistoryRepository.findAllBySessionId(sessionId)
				.stream()
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// remove members who is evicted
			memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(
				MemberStatus.EVICTED));

			// find and get extra information from use-server using uuid
			if (!memberInfoList.isEmpty()) {
				for (MemberInfoResponse memberInfoResponse : memberInfoList) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
						workspaceId, memberInfoResponse.getUuid());
					//todo://user infomation does not have role and role id change to workspace member info
					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					//memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}
				memberInfoList.sort((t1, t2) -> {
					if (t1.getMemberType().equals(MemberType.LEADER)) {
						return 1;
					}
					return 0;
				});
			}

			// Set Member List to Room Detail Information Response
			resultResponse.setMemberList(memberInfoList);
			responseData = new ApiResponse(resultResponse);
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
			TAG,
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

		responseDate = new ApiResponse(resultResponse);

		return responseDate;
	}

	public ApiResponse<ResultResponse> deleteHistoryById(
		String workspaceId
		, RoomHistoryDeleteRequest roomHistoryDeleteRequest
	) {

		ApiResponse<ResultResponse> responseDate;

		LogMessage.formedInfo(
			TAG,
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
					TAG,
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
		responseDate = new ApiResponse(resultResponse);

		return responseDate;
	}
}
