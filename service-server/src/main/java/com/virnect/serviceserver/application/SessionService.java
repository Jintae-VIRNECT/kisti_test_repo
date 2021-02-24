package com.virnect.serviceserver.application;

import static com.virnect.data.dao.RoomSpecifications.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.domain.session.SessionProperty;
import com.virnect.data.domain.session.SessionPropertyHistory;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.remote.application.FileService;
import com.virnect.remote.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.remote.dto.response.member.MemberInfoResponse;
import com.virnect.remote.dto.response.room.RoomDeleteResponse;
import com.virnect.remote.dto.response.room.RoomDetailInfoResponse;
import com.virnect.remote.dto.response.room.RoomInfoListResponse;
import com.virnect.remote.dto.response.room.RoomInfoResponse;
import com.virnect.serviceserver.api.SessionRestController;
import com.virnect.serviceserver.infra.utils.LogMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

	private static final String TAG = SessionRestController.class.getSimpleName();

	private final ModelMapper modelMapper;

	private final WorkspaceRestService workspaceRestService;
	private final RoomRepository roomRepository;
	private final MemberRepository memberRepository;
	private final RoomHistoryRepository roomHistoryRepository;

	private final ServiceSessionManager serviceSessionManager;
	//private final FileDataRepository fileDataRepository;

	private final FileService fileService;


	public RoomInfoListResponse getRoomList(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {

		Page<Room> roomPage = roomRepository.findAll(joinMember(workspaceId, userId), pageable);
		PageMetadataResponse pageMeta;

		for (Room room : roomPage.getContent()) {
			log.info("loadRoomPageList invokeDataProcess: {}", room.getSessionId());
		}

		List<RoomInfoResponse> roomInfoList = new ArrayList<>();
		//for (Room room: roomListMap.keySet()) {
		for (Room room : roomPage.getContent()) {
			RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
			roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

			// Mapping Member List Data to Member Information List
			List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
				.filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// find and get extra information from workspace-server using uuid
			for (MemberInfoResponse memberInfoResponse : memberInfoList) {
				ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
					workspaceId, memberInfoResponse.getUuid());
				log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
				//todo://user infomation does not have role and role id change to workspace member info
				WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
				memberInfoResponse.setRole(workspaceMemberData.getRole());
				//memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
				memberInfoResponse.setEmail(workspaceMemberData.getEmail());
				memberInfoResponse.setName(workspaceMemberData.getName());
				memberInfoResponse.setNickName(workspaceMemberData.getNickName());
				memberInfoResponse.setProfile(workspaceMemberData.getProfile());
			}
			roomInfoResponse.setMemberList(memberInfoList);

			roomInfoList.add(roomInfoResponse);
		}

		if (paging) {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.numberOfElements(roomPage.getNumberOfElements())
				.totalPage(roomPage.getTotalPages())
				.totalElements(roomPage.getTotalElements())
				.last(roomPage.isLast())
				.build();
		} else {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(0)
				.currentSize(0)
				.numberOfElements(roomPage.getNumberOfElements())
				.totalPage(roomPage.getTotalPages())
				.totalElements(roomPage.getTotalElements())
				.last(roomPage.isLast())
				.build();
		}

		return new RoomInfoListResponse(roomInfoList, pageMeta);
	}

	public RoomInfoListResponse getRoomListStandardSearch(
		String workspaceId,
		String userId,
		String search,
		PageRequest pageable
	) {

		ApiResponse<WorkspaceMemberInfoListResponse> feignResponse = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId,
			"remote",
			search,
			pageable.getPageNumber(),
			pageable.getPageSize()
		);

		List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse.getData().getMemberInfoList();
		List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
			.map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
			.collect(Collectors.toList());

		Page<Room> roomPage;
		List<String> userIds = new ArrayList<>();
		for (MemberInfoResponse memberInfo : memberInfoList) {
			if (memberInfo.getUuid() == null || memberInfo.getUuid().isEmpty()) {
				//if memberInfo is empty
				log.info("loadFromDatabase::searchRoomPageList:: some member dose not have uuid");
			} else {
				userIds.add(memberInfo.getUuid());
			}
		}
		if (userIds.isEmpty()) {
			log.info(
				"loadFromDatabase::searchRoomPageList::memberInfoList is empty can not find, search with room title");
			roomPage = roomRepository.findAll(joinMember(workspaceId, userId, search), pageable);
		} else {
			log.info("loadFromDatabase::searchRoomPageList::memberInfoList is not empty");
			roomPage = roomRepository.findAll(joinMember(workspaceId, userId, userIds, search), pageable);
		}

		// Page Metadata
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(roomPage.getNumberOfElements())
			.totalPage(roomPage.getTotalPages())
			.totalElements(roomPage.getTotalElements())
			.last(roomPage.isLast())
			.build();

		for (Room room : roomPage.getContent()) {
			log.info("loadRoomPageList invokeDataProcess: {}", room.getSessionId());
		}

		List<RoomInfoResponse> roomInfoList = new ArrayList<>();
		for (Room room : roomPage.getContent()) {
			RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
			roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

			// Mapping Member List Data to Member Information List
			List<MemberInfoResponse> memberInfos = room.getMembers().stream()
				.filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
				.map(member -> modelMapper.map(member, MemberInfoResponse.class))
				.collect(Collectors.toList());

			// find and get extra information from workspace-server using uuid
			for (MemberInfoResponse memberInfoResponse : memberInfos) {
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
			roomInfoResponse.setMemberList(memberInfos);

			roomInfoList.add(roomInfoResponse);
		}
		return new RoomInfoListResponse(roomInfoList, pageMeta);
	}

	public ApiResponse<RoomDetailInfoResponse> getRoomDetailBySessionId(String workspaceId, String sessionId) {

		ApiResponse<RoomDetailInfoResponse> responseData;

		List<MemberInfoResponse> memberInfoList;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);

		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"loadRoom",
			"room info retrieve by session id",
			sessionId
		);
		if (room == null) {
			responseData = new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
		} else {
			if (room.getRoomStatus() != RoomStatus.ACTIVE) {
				responseData = new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
			} else {
				// mapping data
				RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(
					room, RoomDetailInfoResponse.class);
				roomDetailInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

				// Get Member List by Room Session ID
				// Mapping Member List Data to Member Information List
				memberInfoList = memberRepository.findAllBySessionId(sessionId)
					.stream()
					.filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
					.map(member -> modelMapper.map(member, MemberInfoResponse.class))
					.collect(Collectors.toList());

				// find and get extra information from workspace-server using uuid
				for (MemberInfoResponse memberInfoResponse : memberInfoList) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
						workspaceId, memberInfoResponse.getUuid());
					log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
					//todo://user infomation does not have role and role id change to workspace member info
					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					//memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}

				// Set Member List to Room Detail Information Response
				roomDetailInfoResponse.setMemberList(memberInfoList);
				responseData = new ApiResponse<>(roomDetailInfoResponse);
			}
		}
		return responseData;
	}

	public ApiResponse<RoomDeleteResponse> deleteRoomById(String workspaceId, String sessionId, String userId) {

		ApiResponse<RoomDeleteResponse> responseData;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
		if (room == null) {
			return new ApiResponse<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		//check request user has valid permission
		if (!room.getLeaderId().equals(userId)) {
			return new ApiResponse<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
		}

		for (Member member : room.getMembers()) {
			if (member.getUuid().equals(room.getLeaderId()) && member.getMemberStatus()
				.equals(MemberStatus.LOAD)) {
				return new ApiResponse<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
			}
		}

		log.info("ROOM INFO DELETE BY SESSION ID => [{}]", room.getMembers().size());

		// Set Logging
		RoomHistory roomHistory = RoomHistory.builder()
			.sessionId(room.getSessionId())
			.title(room.getTitle())
			.description(room.getDescription())
			.profile(room.getProfile())
			.leaderId(room.getLeaderId())
			.workspaceId(room.getWorkspaceId())
			.maxUserCount(room.getMaxUserCount())
			.licenseName(room.getLicenseName())
			.build();

		// Remote Session Property Entity Create
		SessionProperty sessionProperty = room.getSessionProperty();
		SessionPropertyHistory sessionPropertyHistory = SessionPropertyHistory.builder()
			.mediaMode(sessionProperty.getMediaMode())
			.recordingMode(sessionProperty.getRecordingMode())
			.defaultOutputMode(sessionProperty.getDefaultOutputMode())
			.defaultRecordingLayout(sessionProperty.getDefaultRecordingLayout())
			.recording(sessionProperty.isRecording())
			.keepalive(sessionProperty.isKeepalive())
			.sessionType(sessionProperty.getSessionType())
			.roomHistory(roomHistory)
			.build();

		roomHistory.setSessionPropertyHistory(sessionPropertyHistory);

		// Set room member history
		// Mapping Member List Data to Member History List
		for (Member roomMember : room.getMembers()) {
			MemberHistory memberHistory = MemberHistory.builder()
				.roomHistory(roomHistory)
				.workspaceId(roomMember.getWorkspaceId())
				.uuid(roomMember.getUuid())
				.memberType(roomMember.getMemberType())
				.deviceType(roomMember.getDeviceType())
				.sessionId(roomMember.getSessionId())
				.startDate(roomMember.getStartDate())
				.endDate(roomMember.getEndDate())
				.durationSec(roomMember.getDurationSec())
				.build();

			//sessionService.setMemberHistory(memberHistory);
			roomHistory.getMemberHistories().add(memberHistory);

			//delete member
			memberRepository.delete(roomMember);
		}

		//set active time
		roomHistory.setActiveDate(room.getActiveDate());

		//set un active  time
		LocalDateTime endTime = LocalDateTime.now();
		roomHistory.setUnactiveDate(endTime);

		//time diff seconds
		Duration duration = Duration.between(room.getActiveDate(), endTime);
		roomHistory.setDurationSec(duration.getSeconds());

		//save room history
		boolean result = roomHistoryRepository.existsByWorkspaceIdAndSessionId(
			roomHistory.getWorkspaceId(), roomHistory.getSessionId());
		if (result) {
			log.error("Duplicate entry {}", roomHistory.getSessionId());
		} else {
			this.roomHistoryRepository.save(roomHistory);
		}

		// end Set Logging
		roomRepository.delete(room);

		responseData = new ApiResponse<>(new RoomDeleteResponse(
			sessionId,
			true,
			LocalDateTime.now()
		));

		if (responseData.getData().result) {
			//send rpc message to connection id user of the session id
            /*DataProcess<List<String>> dataProcess = this.sessionDataRepository.getConnectionIds(workspaceId, sessionId);
            JsonObject jsonObject = serviceSessionManager.generateMessage(
                    sessionId,
                    dataProcess.getData(),
                    PushConstants.PUSH_SIGNAL_SYSTEM,
                    PushConstants.SEND_PUSH_ROOM_CLOSED
            );*/

			if (this.serviceSessionManager.closeActiveSession(sessionId)) {
				LogMessage.formedInfo(
					TAG,
					"serviceSessionManager",
					"closeActiveSession"
				);
				fileService.removeFiles(workspaceId, sessionId);
				//fileDataRepository.removeFiles(workspaceId, sessionId);
				//return ResponseEntity.ok(apiResponse);
			}

			if (this.serviceSessionManager.closeNotActiveSession(sessionId)) {
				LogMessage.formedInfo(
					TAG,
					"serviceSessionManager",
					"closeNotActiveSession"
				);
				fileService.removeFiles(workspaceId, sessionId);
				//this.fileDataRepository.removeFiles(workspaceId, sessionId);
				//return ResponseEntity.ok(apiResponse);
			}
		}
		return responseData;
	}

	public ApiResponse<RoomDetailInfoResponse> updateRoom(
		String workspaceId,
		String sessionId,
		ModifyRoomInfoRequest modifyRoomInfoRequest
	) {

		ApiResponse<RoomDetailInfoResponse> responseData;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
		List<MemberInfoResponse> memberInfoList;

		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"updateRoom",
			"room info retrieve by session id",
			sessionId
		);

		String userId = room.getLeaderId();
		if (room != null) {
			if (userId.equals(modifyRoomInfoRequest.getUuid())) {

				room.setTitle(modifyRoomInfoRequest.getTitle());
				room.setDescription(modifyRoomInfoRequest.getDescription());

				Room updatedRoom = roomRepository.save(room);

				// mapping data
				RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(
					updatedRoom, RoomDetailInfoResponse.class);
				roomDetailInfoResponse.setSessionType(updatedRoom.getSessionProperty().getSessionType());
				// Get Member List by Room Session ID
				// Mapping Member List Data to Member Information List
				memberInfoList = memberRepository.findAllBySessionId(sessionId)
					.stream()
					.filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
					.map(member -> modelMapper.map(member, MemberInfoResponse.class))
					.collect(Collectors.toList());

				// find and get extra information from use-server using uuid
				for (MemberInfoResponse memberInfoResponse : memberInfoList) {
					ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
						workspaceId, memberInfoResponse.getUuid());
					log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
					//todo://user infomation does not have role and role id change to workspace member info
					WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
					memberInfoResponse.setRole(workspaceMemberData.getRole());
					//memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
					memberInfoResponse.setEmail(workspaceMemberData.getEmail());
					memberInfoResponse.setName(workspaceMemberData.getName());
					memberInfoResponse.setNickName(workspaceMemberData.getNickName());
					memberInfoResponse.setProfile(workspaceMemberData.getProfile());
				}

				// Set Member List to Room Detail Information Response
				roomDetailInfoResponse.setMemberList(memberInfoList);
				responseData = new ApiResponse<>(roomDetailInfoResponse);
			} else {
				responseData = new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
			}
		} else {
			responseData = new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		return responseData;
	}
}
