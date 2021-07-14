package com.virnect.serviceserver.serviceremote.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.dao.roomhistory.RoomHistoryRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.domain.session.SessionProperty;
import com.virnect.data.domain.session.SessionPropertyHistory;
import com.virnect.data.domain.session.SessionType;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.constraint.LicenseItem;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.room.RoomDetailMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.room.RoomInfoMapper;
import com.virnect.data.dto.push.SendSignalRequest;
import com.virnect.data.dto.request.room.InviteRoomRequest;
import com.virnect.data.dto.request.room.JoinRoomRequest;
import com.virnect.data.dto.request.room.KickRoomRequest;
import com.virnect.data.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.data.dto.request.room.RoomRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.response.room.CoturnResponse;
import com.virnect.data.dto.response.room.InviteRoomResponse;
import com.virnect.data.dto.response.room.KickRoomResponse;
import com.virnect.data.dto.response.room.RoomDeleteResponse;
import com.virnect.data.dto.response.room.RoomDetailInfoResponse;
import com.virnect.data.dto.response.room.RoomInfoListResponse;
import com.virnect.data.dto.response.room.RoomInfoResponse;
import com.virnect.data.dto.response.room.RoomResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.serviceremote.api.SessionRestController;
import com.virnect.serviceserver.serviceremote.dao.SessionDataRepository;
import com.virnect.serviceserver.serviceremote.dto.constraint.PushConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

	private static final String TAG = SessionRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/room";

	private final RoomRepository roomRepository;
	private final SessionDataRepository sessionDataRepository;
	private final RoomHistoryRepository roomHistoryRepository;
	private final WorkspaceRestService workspaceRestService;
	private final MemberRepository memberRepository;
	private final ServiceSessionManager serviceSessionManager;
	private final FileService fileService;

	private final RoomInfoMapper roomInfoMapper;
	private final RoomDetailMapper roomDetailMapper;
	private final MemberMapper memberMapper;

	private final AccessStatusService accessStatusService;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private final RemoteServiceConfig config;

	private List<MemberInfoResponse> setLeader(List<MemberInfoResponse> members) {
		members.sort((t1, t2) -> {
			int index1 = t1.getMemberType().ordinal();
			int index2 = t2.getMemberType().ordinal();
			return Integer.compare(index1, index2);
		});
		return members;
	}

	private boolean IsValidUserCapacity(RoomRequest roomRequest, LicenseItem licenseItem) {
		// check room request member count is over
		return roomRequest.getParticipantIds().size() + 1 <= licenseItem.getUserCapacity();
	}

	public ApiResponse<RoomResponse> redialRoomRequest(
		RoomRequest roomRequest,
		String sessionId,
		int companyCode
	) {
		
		// 유효 라이센스 확인
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (ObjectUtils.isEmpty(licenseItem)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}

		// 오픈방이 아닐 경우 인원수 체크
		if (roomRequest.getSessionType() != SessionType.OPEN) {
			if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
				return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
			}
		}

		// generate session id and token
		JsonObject sessionJson = serviceSessionManager.generateSession();
		JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

		return this.sessionDataRepository.generateRoom(
			sessionId,
			roomRequest,
			licenseItem,
			roomRequest.getLeaderId(),
			sessionJson.toString(),
			tokenResult.toString()
		);
	}

	public ApiResponse<RoomResponse> redialRoomRequestByUserId(
		String client,
		String userId,
		RoomRequest roomRequest,
		String sessionId,
		int companyCode
	) {

		// 유효 라이센스 확인
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (ObjectUtils.isEmpty(licenseItem)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}

		// 오픈방이 아닐 경우 인원수 체크
		if (roomRequest.getSessionType() != SessionType.OPEN) {
			if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
				return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
			}
		}

		// 세션 및 토큰 생성
		JsonObject sessionJson = serviceSessionManager.generateSession();
		JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

		// 협업방 생성
		return this.sessionDataRepository.generateRoom(
			sessionId,
			roomRequest,
			licenseItem,
			userId,
			sessionJson.toString(),
			tokenResult.toString()
		);
	}

	public ApiResponse<RoomResponse> initRoomByClient(
		String client,
		String userId,
		RoomRequest roomRequest,
		int companyCode
	) {

		/**
		 * 1. check room request handler
		 * 2. check user license type using user uuid
		 * 3. generate session id and token
		 * 4. create room
		 * 5. register user as a leader who creates the room
		 * 6. register other users as a worker(participant), if the request contains other user information.
		 * 7. return session id and token
		 */
		
		// 유효 라이센스 확인
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (ObjectUtils.isEmpty(licenseItem)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}
		// 오픈방이 아닐 경우 인원수 체크
		if (roomRequest.getSessionType() != SessionType.OPEN) {
			if (!IsValidUserCapacity(roomRequest, licenseItem)) {
				return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
			}
		}

		// 세션 및 토큰 생성
		JsonObject sessionJson = serviceSessionManager.generateSession();
		JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

		// 협업방 생성
		ApiResponse<RoomResponse> responseData = this.sessionDataRepository.generateRoom(
			roomRequest,
			licenseItem,
			userId,
			sessionJson.toString(),
			tokenResult.toString()
		);
		responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));

		return responseData;
	}

	public ApiResponse<RoomResponse> initRoom(
		RoomRequest roomRequest,
		int companyCode
	) {
		/**
		 * 1. check room request handler
		 * 2. check user license type using user uuid
		 * 3. generate session id and token
		 * 4. create room
		 * 5. register user as a leader who creates the room
		 * 6. register other users as a worker(participant), if the request contains other user information.
		 * 7. return session id and token
		 */
		
		// 유효 라이센스 확인
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (ObjectUtils.isEmpty(licenseItem)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}

		// 오픈방이 아닐 경우 정원 인원수 확인
		if (!(roomRequest.getSessionType() == SessionType.OPEN)) {
			if (!IsValidUserCapacity(roomRequest, licenseItem)) {
				return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_IS_OVER );
			}
		}

		// 세션 및 토큰 생성
		JsonObject sessionJson = serviceSessionManager.generateSession();
		JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);
		
		// 협업방 생성
		ApiResponse<RoomResponse> responseData = this.sessionDataRepository.generateRoom(
			roomRequest,
			licenseItem,
			roomRequest.getLeaderId(),
			sessionJson.toString(),
			tokenResult.toString()
		);
		responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
		return responseData;
	}

	public ApiResponse<RoomResponse> joinRoomById(
		String workspaceId,
		String sessionId,
		JoinRoomRequest joinRoomRequest
	) {
		/*
		 * joinRoomById
		 * 1. DB 데이터 처리 (해당 세션 참여 멤버 리스트 체크, 해당 멤버 상태 체크)
		 * 2. Kurento Media server 간 세션 처리
		 * 3. Create Response Data
		 */
		
		// 생성 전 DB를 통한 데이트 체크
		ApiResponse<Boolean> dataProcess = this.sessionDataRepository.prepareJoinRoom(
			workspaceId,
			sessionId,
			joinRoomRequest.getUuid()
		);

		if (!dataProcess.getData()) {
			LogMessage.formedInfo(
				TAG,
				"REST API: POST " + REST_PATH + "/",
				"joinRoomById",
				"process data get false",
				dataProcess.getMessage()
			);
			return new ApiResponse<>(dataProcess.getCode(), dataProcess.getMessage());
		}

		// 세션 및 토큰 생성
		JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
		JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

		// 협업방 참여
		ApiResponse<RoomResponse> responseData = this.sessionDataRepository.joinRoom(
			workspaceId,
			sessionId,
			tokenResult.toString(),
			joinRoomRequest
		);
		responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));

		return responseData;
	}

	public ApiResponse<ResultResponse> exitRoomBySessionIdAndUserId(
		String workspaceId,
		String sessionId,
		String userId
	) {
		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		Member member = null;
		for (Member participant : room.getMembers()) {
			if (participant.getUuid().equals(userId)) {
				member = participant;
			}
		}

		if (ObjectUtils.isEmpty(member)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
		}

		ErrorCode errorCode = ErrorCode.ERR_SUCCESS;
		if (member.getMemberType() == MemberType.LEADER) {
			errorCode = ErrorCode.ERR_ROOM_LEADER_INVALID_EXIT;
		} else if (member.getMemberStatus() == MemberStatus.LOAD) {
			errorCode = ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
		}

		if (errorCode != ErrorCode.ERR_SUCCESS) {
			return new ApiResponse<>(errorCode);
		}

		room.getMembers().remove(member);
		roomRepository.save(room);

		ResultResponse resultResponse = new ResultResponse();
		resultResponse.setUserId(userId);
		resultResponse.setResult(true);

		return new ApiResponse<>(resultResponse);
	}

	public ApiResponse<ResultResponse> inviteMember(
		String workspaceId,
		String sessionId,
		InviteRoomRequest inviteRoomRequest
	) {

		ApiResponse<InviteRoomResponse> response = this.sessionDataRepository.inviteMember(
			workspaceId,
			sessionId,
			inviteRoomRequest
		);

		if (!(response.getCode() == ErrorCode.ERR_SUCCESS.getCode())) {
			return new ApiResponse<>(
				new ResultResponse(),
				response.getCode(),
				response.getMessage()
			);
		}

		this.sessionDataRepository.sendInviteMessage(response.getData());

		return new ApiResponse<>(new ResultResponse(
			inviteRoomRequest.getLeaderId(),
		true,
			LocalDateTime.now(),
			new HashMap<>())
		);
	}

	public ApiResponse<ResultResponse> kickOutMember(
		String workspaceId,
		String sessionId,
		KickRoomRequest kickRoomRequest
	) {
		ApiResponse<KickRoomResponse> apiResponse = this.sessionDataRepository.kickFromRoom(
			workspaceId,
			sessionId,
			kickRoomRequest
		);

		String connectionId = apiResponse.getData().getConnectionId();

		if (apiResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
			return new ApiResponse<>(apiResponse.getCode(), apiResponse.getMessage());
		}

		//send push message
		if (StringUtils.isBlank(connectionId)) {
			this.sessionDataRepository.sendEvictMessage(apiResponse.getData());
			return new ApiResponse<>(
				new ResultResponse(kickRoomRequest.getLeaderId(),
				true,
				LocalDateTime.now(),
				new HashMap<>())
			);
		}
		//send rpc message to connection id user of the session id
		JsonObject jsonObject = serviceSessionManager.generateMessage(
			sessionId,
			Collections.singletonList(connectionId),
			PushConstants.PUSH_SIGNAL_SYSTEM,
			PushConstants.SEND_PUSH_ROOM_EVICT
		);

		if (jsonObject.has("error")) {
			log.info("sendSignal :{}", jsonObject.get("error").getAsString());
			log.info("sendSignal :{}", jsonObject.get("status").getAsString());
			log.info("sendSignal :{}", jsonObject.get("message").getAsString());
			new ApiResponse<>(
				new ResultResponse(),
				Integer.parseInt(jsonObject.get("status").getAsString()),
				jsonObject.get("message").getAsString());
		}

		log.info("Evict participant result : {}", serviceSessionManager.evictParticipant(sessionId, connectionId));

		return new ApiResponse<>(
			new ResultResponse(kickRoomRequest.getLeaderId(),
				true,
				LocalDateTime.now(),
				new HashMap<>())
		);
	}

	public ApiResponse<ResultResponse> sendSignal(String workspaceId, SendSignalRequest sendSignalRequest) {

		ApiResponse<ResultResponse> apiResponse = new ApiResponse<>();

		JsonObject jsonObject = serviceSessionManager.generateMessage(
			sendSignalRequest.getSessionId(),
			sendSignalRequest.getTo(),
			sendSignalRequest.getType(),
			sendSignalRequest.getData()
		);
		if (jsonObject.has("error")) {
			log.info("sendSignal :{}", jsonObject.get("error").getAsString());
			log.info("sendSignal :{}", jsonObject.get("status").getAsString());
			log.info("sendSignal :{}", jsonObject.get("message").getAsString());
			apiResponse.setCode(Integer.parseInt(jsonObject.get("status").getAsString()));
			apiResponse.setMessage(jsonObject.get("message").getAsString());
		} else {
			apiResponse.getData().setResult(true);
		}
		apiResponse.setData(new ResultResponse());
		return apiResponse;
	}

	private CoturnResponse setCoturnResponse(SessionType sessionType) {
		CoturnResponse coturnResponse = new CoturnResponse();
		switch (sessionType) {
			case OPEN: {
				List<String> urlList = config.remoteServiceProperties.getCoturnUrisStreaming();
				if (urlList.isEmpty()) {
					for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
						coturnResponse.setUsername(config.remoteServiceProperties.getCoturnName());
						coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
						coturnResponse.setUrl(coturnUrl);
					}
				} else {
					for (String coturnUrl : urlList) {
						coturnResponse.setUsername(config.remoteServiceProperties.getCoturnName());
						coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
						coturnResponse.setUrl(coturnUrl);
					}
				}
			}
			break;
			case PUBLIC:
			case PRIVATE: {
				for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
					coturnResponse.setUsername(config.remoteServiceProperties.getCoturnName());
					coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
					//coturnResponse.setUrl(coturnUrl.replaceAll("\"", ""));
					coturnResponse.setUrl(coturnUrl);
				}
			}
			break;
		}
		return coturnResponse;
	}

	public ApiResponse<RoomDetailInfoResponse> getRoomDetailBySessionId(String workspaceId, String sessionId) {

		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"loadRoom",
			"room info retrieve by session id",
			sessionId
		);

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdNotInEvictedMember(workspaceId, sessionId).orElse(null);
		if(ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		if (room.getRoomStatus() != RoomStatus.ACTIVE) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
		}

		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (Member member : room.getMembers()) {
			if (!(member.getUuid() == null || member.getUuid().isEmpty())) {
				userList.add(member.getUuid());
			}
		}
		String[] userIds = userList.stream().distinct().toArray(String[]::new);

		// Receive User list from Workspace
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMemberInfoList(workspaceId, userIds);

		// mapping data
		RoomDetailInfoResponse roomDetailInfoResponse = roomDetailMapper.toDto(room);
		roomDetailInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

		List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
			.map(memberMapper::toDto)
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

		// Redis 내 멤버 접속상태 확인
		for (MemberInfoResponse memberInfoResponse : memberInfoList) {
			memberInfoResponse.setAccessType(loadAccessType(workspaceId, memberInfoResponse.getUuid()));
		}

		roomDetailInfoResponse.setMemberList(setLeader(memberInfoList));
		return new ApiResponse<>(roomDetailInfoResponse);
	}

	public RoomInfoListResponse getRoomList(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {

		PageMetadataResponse pageMeta;
		Page<Room> roomPage = roomRepository.findMyRoomSpecificUserId(workspaceId, userId, paging, pageable);

		if (roomPage.getContent().isEmpty()) {
			List<RoomInfoResponse> emptyList = new ArrayList<>();
			pageMeta = PageMetadataResponse.builder()
				.currentPage(0)
				.currentSize(0)
				.numberOfElements(0)
				.totalPage(0)
				.totalElements(0)
				.last(true)
				.build();
			return new RoomInfoListResponse(emptyList, pageMeta);
		}

		List<RoomInfoResponse> roomInfoResponses = makeRoomInfoResponse(workspaceId, roomPage);

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
				.totalPage(1)
				.totalElements(roomInfoResponses.size())
				.last(true)
				.build();
		}
		return new RoomInfoListResponse(roomInfoResponses, pageMeta);
	}

	public RoomInfoListResponse getRoomListStandardSearch(
		String workspaceId,
		String userId,
		String search,
		PageRequest pageable
	) {

		List<String> userIds = new ArrayList<>();
		List<WorkspaceMemberInfoResponse> members = workspaceRestService.getWorkspaceMemberInfoList(
			workspaceId,
			"remote",
			search,
			Integer.MAX_VALUE
		).getData().getMemberInfoList();
		for (WorkspaceMemberInfoResponse memberInfo : members) {
			if (StringUtils.isBlank(memberInfo.getUuid())) {
				//if memberInfo is empty
				log.info("loadFromDatabase::searchRoomHistoryPageList:: some member dose not have uuid");
			} else {
				userIds.add(memberInfo.getUuid());
			}
		}

		PageMetadataResponse pageMeta;
		Page<Room>  roomPage = roomRepository.findMyRoomSpecificUserIdBySearch(workspaceId, userId, userIds, search, pageable);

		if (roomPage.getContent().isEmpty()) {
			List<RoomInfoResponse> emptyList = new ArrayList<>();
			pageMeta = PageMetadataResponse.builder()
				.currentPage(0)
				.currentSize(0)
				.numberOfElements(0)
				.totalPage(0)
				.totalElements(0)
				.last(true)
				.build();
			return new RoomInfoListResponse(emptyList, pageMeta);
		}

		List<RoomInfoResponse> roomInfoResponses = makeRoomInfoResponse(workspaceId, roomPage);

		pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.numberOfElements(roomPage.getNumberOfElements())
				.totalPage(roomPage.getTotalPages())
				.totalElements(roomPage.getTotalElements())
				.last(roomPage.isLast())
				.build();

		return new RoomInfoListResponse(roomInfoResponses, pageMeta);
	}

	public ApiResponse<RoomDetailInfoResponse> updateRoom(
		String workspaceId,
		String sessionId,
		ModifyRoomInfoRequest modifyRoomInfoRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"updateRoom",
			"room info retrieve by session id",
			sessionId
		);

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		if (!room.getLeaderId().equals(modifyRoomInfoRequest.getUuid())) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
		}

		room.setTitle(modifyRoomInfoRequest.getTitle());
		room.setDescription(modifyRoomInfoRequest.getDescription());

		Room updatedRoom = roomRepository.save(room);

		// mapping data
		RoomDetailInfoResponse roomDetailInfoResponse = roomDetailMapper.toDto(room);
		roomDetailInfoResponse.setSessionType(updatedRoom.getSessionProperty().getSessionType());
		// Get Member List by Room Session ID
		// Mapping Member List Data to Member Information List
		List<MemberInfoResponse> memberInfoList = memberRepository.findAllBySessionId(sessionId)
			.stream()
			.filter(member -> !(member.getMemberStatus() == MemberStatus.EVICTED))
			.map(memberMapper::toDto)
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
		return new ApiResponse<>(roomDetailInfoResponse);
	}

	public ApiResponse<RoomDeleteResponse> deleteRoomById(String workspaceId, String sessionId, String userId) {

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		//check request user has valid permission
		if (!room.getLeaderId().equals(userId)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
		}
		for (Member member : room.getMembers()) {
			if (member.getUuid().equals(room.getLeaderId()) && member.getMemberStatus() == MemberStatus.LOAD) {
				return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
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

		if (this.serviceSessionManager.closeActiveSession(sessionId)) {
			LogMessage.formedInfo(
				TAG,
				"serviceSessionManager",
				"closeActiveSession"
			);
			fileService.removeFiles(workspaceId, sessionId);
		}

		if (this.serviceSessionManager.closeNotActiveSession(sessionId)) {
			LogMessage.formedInfo(
				TAG,
				"serviceSessionManager",
				"closeNotActiveSession"
			);
			fileService.removeFiles(workspaceId, sessionId);
		}
		return new ApiResponse<>(new RoomDeleteResponse(sessionId, true, LocalDateTime.now()));
	}

	private List<RoomInfoResponse> makeRoomInfoResponse(
		String workspaceId,
		Page<Room> roomPage
	) {

		List<RoomInfoResponse> roomInfoResponses = new ArrayList<>();
		// Make uuid array
		List<String> userList = new ArrayList<>();
		for (Room room : roomPage) {
			for (Member member : room.getMembers()) {
				if (!(member.getUuid() == null || member.getUuid().isEmpty())) {
					userList.add(member.getUuid());
				}
			}
		}

		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = null;
		// Receive User list from Workspace
		if (!CollectionUtils.isEmpty(userList)) {
			memberInfo = workspaceRestService.getWorkspaceMemberInfoList(workspaceId, userList.stream().distinct().toArray(String[]::new));
		}

		// Make Response data
		for (Room room : roomPage.getContent()) {
			RoomInfoResponse roomInfoResponse = roomInfoMapper.toDto(room);
			roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

			// Mapping Member List Data to Member Information List
			List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
				.map(memberMapper::toDto)
				.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(memberInfo.getData().getMemberInfoList())) {
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
			}
			roomInfoResponse.setMemberList(setLeader(memberInfoList));
			roomInfoResponses.add(roomInfoResponse);
		}
		return roomInfoResponses;
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
