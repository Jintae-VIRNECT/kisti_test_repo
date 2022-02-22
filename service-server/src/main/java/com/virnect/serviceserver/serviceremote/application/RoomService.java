package com.virnect.serviceserver.serviceremote.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.virnect.data.error.exception.RemoteServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.global.util.paging.PagingUtils;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.serviceremote.dao.SessionDataRepository;
import com.virnect.serviceserver.serviceremote.dto.constraint.PushConstants;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.room.RoomDetailMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.room.RoomInfoMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

	private static final String TAG = RoomService.class.getSimpleName();
	private static final String REST_PATH = "/remote/room";

	private final RemoteServiceConfig config;

	private final RoomRepository roomRepository;
	private final SessionDataRepository sessionDataRepository;
	private final RoomHistoryRepository roomHistoryRepository;
	private final MemberRepository memberRepository;

	private final WorkspaceRestService workspaceRestService;

	private final ServiceSessionManager serviceSessionManager;

	private final AccessStatusService accessStatusService;
	private final FileService fileService;

	private final RoomInfoMapper roomInfoMapper;
	private final RoomDetailMapper roomDetailMapper;
	private final MemberMapper memberMapper;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private List<MemberInfoResponse> setLeader(List<MemberInfoResponse> members) {
		members.sort((t1, t2) -> {
			int index1 = t1.getMemberType().ordinal();
			int index2 = t2.getMemberType().ordinal();
			return Integer.compare(index1, index2);
		});
		return members;
	}

	private boolean isValidUserCapacity(RoomRequest roomRequest, LicenseItem licenseItem) {
		// check room request member count is over
		return roomRequest.getParticipantIds().size() + 1 <= licenseItem.getUserCapacity();
	}

	public RoomResponse redialRoomRequest(
		RoomRequest roomRequest,
		String sessionId,
		int companyCode
	) {

		// 유효 라이센스 확인
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (ObjectUtils.isEmpty(licenseItem)) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}

		// 오픈방이 아닐 경우 인원수 체크
		if (roomRequest.getSessionType() != SessionType.OPEN) {
			if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
				throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
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
		).getData();
	}

	public RoomResponse redialRoomRequestByUserId(
		String client,
		String userId,
		RoomRequest roomRequest,
		String sessionId,
		int companyCode
	) {

		// 유효 라이센스 확인
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (ObjectUtils.isEmpty(licenseItem)) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}

		// 오픈방이 아닐 경우 인원수 체크
		if (roomRequest.getSessionType() != SessionType.OPEN) {
			if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
				throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
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
		).getData();
	}

	public RoomResponse initRoomByClient(
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
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}
		// 오픈방이 아닐 경우 인원수 체크
		if (roomRequest.getSessionType() != SessionType.OPEN) {
			if (!isValidUserCapacity(roomRequest, licenseItem)) {
				throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
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
		//responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
		responseData.getData().setCoturn(setCoturnListResponse(responseData.getData().getSessionType()));
		return responseData.getData();
	}

	public RoomResponse initRoom(
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
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}

		// 오픈방이 아닐 경우 정원 인원수 확인
		if (!(roomRequest.getSessionType() == SessionType.OPEN)) {
			if (!isValidUserCapacity(roomRequest, licenseItem)) {
				throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
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
		//responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
		responseData.getData().setCoturn(setCoturnListResponse(responseData.getData().getSessionType()));
		return responseData.getData();
	}

	public RoomResponse joinRoomById(
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
		this.sessionDataRepository.prepareJoinRoom(workspaceId, sessionId, joinRoomRequest.getUuid());

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
		//responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
		responseData.getData().setCoturn(setCoturnListResponse(responseData.getData().getSessionType()));

		return responseData.getData();
	}

	public ResultResponse exitRoomBySessionIdAndUserId(
		String workspaceId,
		String sessionId,
		String userId
	) {
		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		Member member = null;
		for (Member participant : room.getMembers()) {
			if (participant.getUuid().equals(userId)) {
				member = participant;
			}
		}

		if (ObjectUtils.isEmpty(member)) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
		}

		ErrorCode errorCode = ErrorCode.ERR_SUCCESS;
		if (member.getMemberType() == MemberType.LEADER) {
			errorCode = ErrorCode.ERR_ROOM_LEADER_INVALID_EXIT;
		} else if (member.getMemberStatus() == MemberStatus.LOAD) {
			errorCode = ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
		}

		if (errorCode != ErrorCode.ERR_SUCCESS) {
			throw new RemoteServiceException(errorCode);
		}

		room.getMembers().remove(member);
		roomRepository.save(room);

		return ResultResponse.builder()
			.userId(userId)
			.result(true)
			.build();

	}

	public ResultResponse inviteMember(
		String workspaceId,
		String sessionId,
		InviteRoomRequest inviteRoomRequest
	) {
		InviteRoomResponse response = this.sessionDataRepository.inviteMember(
			workspaceId,
			sessionId,
			inviteRoomRequest
		);
		this.sessionDataRepository.sendInviteMessage(response);

		return ResultResponse.builder()
			.userId(inviteRoomRequest.getLeaderId())
			.result(true)
			.build();
	}

	public ResultResponse kickOutMember(
		String workspaceId,
		String sessionId,
		KickRoomRequest kickRoomRequest
	) {
		KickRoomResponse kickRoomResponse = this.sessionDataRepository.kickFromRoom(
			workspaceId,
			sessionId,
			kickRoomRequest
		);

		String connectionId = kickRoomResponse.getConnectionId();
		//send push message
		if (StringUtils.isBlank(connectionId)) {
			this.sessionDataRepository.sendEvictMessage(kickRoomResponse);
			return ResultResponse.builder()
				.userId(kickRoomRequest.getLeaderId())
				.result(true)
				.build();
		}
		//send rpc message to connection id user of the session id
		serviceSessionManager.generateMessage(
			sessionId,
			Collections.singletonList(connectionId),
			PushConstants.PUSH_SIGNAL_SYSTEM,
			PushConstants.SEND_PUSH_ROOM_EVICT
		);

		log.info("Evict participant result : {}", serviceSessionManager.evictParticipant(sessionId, connectionId));

		return ResultResponse.builder()
			.userId(kickRoomRequest.getLeaderId())
			.result(true)
			.build();
	}

	public List<CoturnResponse> setCoturnListResponse(SessionType sessionType) {
		List<CoturnResponse> coturnList = new ArrayList<>();
		switch (sessionType) {
			case OPEN: {
				List<String> urlList = config.remoteServiceProperties.getCoturnUrisStreaming();
				if (urlList.isEmpty()) {
					for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
						coturnList.add(CoturnResponse.builder()
							.username(config.remoteServiceProperties.getCoturnName())
							.credential(config.remoteServiceProperties.getCoturnCredential())
							.url(coturnUrl)
							.build()
						);
					}
				} else {
					for (String coturnUrl : urlList) {
						coturnList.add(CoturnResponse.builder()
							.username(config.remoteServiceProperties.getCoturnName())
							.credential(config.remoteServiceProperties.getCoturnCredential())
							.url(coturnUrl)
							.build()
						);
					}
				}
			}
			break;
			case PUBLIC:
			case PRIVATE: {
				for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
					coturnList.add(CoturnResponse.builder()
						.username(config.remoteServiceProperties.getCoturnName())
						.credential(config.remoteServiceProperties.getCoturnCredential())
						.url(coturnUrl)
						.build());
				}
			}
			break;
		}
		return coturnList;
	}

	public RoomDetailInfoResponse getRoomDetailBySessionId(String workspaceId, String sessionId) {

		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"loadRoom",
			"room info retrieve by session id",
			sessionId
		);

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdNotInEvictedMember(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		if (room.getRoomStatus() != RoomStatus.ACTIVE) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
		}

		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMembersExcludeUserIds(
			workspaceId,
			room.getMembers().stream().map(Member::getUuid).distinct().toArray(String[]::new)
		);

		// mapping data
		RoomDetailInfoResponse roomDetailInfoResponse = roomDetailMapper.toDto(room);
		roomDetailInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

		List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
			.map(memberMapper::toDto)
			.collect(Collectors.toList());

		mapperWorkspaceMemberToMember(memberInfo, memberInfoList);

		// Redis 내 멤버 접속상태 확인
		for (MemberInfoResponse memberInfoResponse : memberInfoList) {
			memberInfoResponse.setAccessType(loadAccessType(workspaceId, memberInfoResponse.getUuid()));
		}

		roomDetailInfoResponse.setMemberList(setLeader(memberInfoList));
		return roomDetailInfoResponse;
	}

	static void mapperWorkspaceMemberToMember(
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo,
		List<MemberInfoResponse> memberInfoList
	) {
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

	public RoomInfoListResponse getRoomList(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {

		Page<Room> roomPage = roomRepository.findMyRoomSpecificUserId(workspaceId, userId, paging, pageable);
		if (roomPage.getContent().isEmpty()) {
			return new RoomInfoListResponse(new ArrayList<>(), PagingUtils.emptyPagingBuilder());
		}

		List<RoomInfoResponse> roomInfoResponses = makeRoomsInfo(workspaceId, roomPage);
		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			paging,
			pageable,
			roomPage.getNumberOfElements(),
			roomPage.getTotalPages(),
			roomPage.getTotalElements(),
			roomPage.isLast()
		);

		return new RoomInfoListResponse(roomInfoResponses, pageMeta);
	}

	public RoomInfoListResponse getRoomListStandardSearch(
		String workspaceId,
		String userId,
		String search,
		PageRequest pageable
	) {

		List<WorkspaceMemberInfoResponse> workspaceMembers = workspaceRestService.getWorkspaceMembers(
			workspaceId,
			"remote",
			search,
			50
		).getData().getMemberInfoList();

		Set<String> userIds = workspaceMembers.stream()
			.map(WorkspaceMemberInfoResponse::getUuid)
			.collect(Collectors.toSet());

		//List<String> userIds = new ArrayList<>();
		/*for (WorkspaceMemberInfoResponse memberInfo : workspaceMembers) {
			if (StringUtils.isBlank(memberInfo.getUuid())) {
				//if memberInfo is empty
				log.info("loadFromDatabase::searchRoomHistoryPageList:: some member dose not have uuid");
			} else {
				userIds.add(memberInfo.getUuid());
			}
		}*/

		Page<Room> roomPage = roomRepository.findMyRoomSpecificUserIdBySearch(
			workspaceId,
			userId,
			new ArrayList<>(userIds),
			search,
			pageable
		);
		if (roomPage.getContent().isEmpty()) {
			return new RoomInfoListResponse(new ArrayList<>(), PageMetadataResponse.builder().build());
		}

		List<RoomInfoResponse> roomInfoResponses = makeRoomsInfo(workspaceId, roomPage);

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			true,
			pageable,
			roomPage.getNumberOfElements(),
			roomPage.getTotalPages(),
			roomPage.getTotalElements(),
			roomPage.isLast()
		);

		return new RoomInfoListResponse(roomInfoResponses, pageMeta);
	}

	public RoomDetailInfoResponse updateRoom(
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

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		if (!room.getLeaderId().equals(modifyRoomInfoRequest.getUuid())) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
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
			ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMember(
				workspaceId, memberInfoResponse.getUuid());
			log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());

			WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
			memberInfoResponse.setRole(workspaceMemberData.getRole());
			memberInfoResponse.setEmail(workspaceMemberData.getEmail());
			memberInfoResponse.setName(workspaceMemberData.getName());
			memberInfoResponse.setNickName(workspaceMemberData.getNickName());
			memberInfoResponse.setProfile(workspaceMemberData.getProfile());
		}

		// Set Member List to Room Detail Information Response
		roomDetailInfoResponse.setMemberList(memberInfoList);
		return roomDetailInfoResponse;
	}

	public RoomDeleteResponse deleteRoomById(String workspaceId, String sessionId, String userId) {

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		//check request user has valid permission
		if (!room.getLeaderId().equals(userId)) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
		}
		for (Member member : room.getMembers()) {
			if (member.getUuid().equals(room.getLeaderId()) && member.getMemberStatus() == MemberStatus.LOAD) {
				throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
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

			accessStatusService.saveAccessStatus(
				roomMember.getWorkspaceId(),
				roomMember.getUuid(),
				AccessType.LOGIN
			);

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

		return new RoomDeleteResponse(sessionId, true, LocalDateTime.now());
	}

	public RoomInfoResponse makeRoomInfo(
		String workspaceId,
		Room room
	) {
		/*// Make uuid array
		List<String> userList = new ArrayList<>();
		for (Member member : room.getMembers()) {
			if (!(StringUtils.isBlank(member.getUuid()))) {
				userList.add(member.getUuid());
			}
		}*/
		// Receive User list from Workspace
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMembersExcludeUserIds(
			workspaceId,
			room.getMembers().stream().map(Member::getUuid).distinct().toArray(String[]::new)
		);

		// Make Response data
		RoomInfoResponse roomInfoResponse = roomInfoMapper.toDto(room);
		roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

		// Mapping Member List Data to Member Information List
		List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
			.map(memberMapper::toDto)
			.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(memberInfo.getData().getMemberInfoList())) {
			mapperWorkspaceMemberToMember(memberInfo, memberInfoList);
		}
		roomInfoResponse.setMemberList(setLeader(memberInfoList));
		return roomInfoResponse;
	}

	private List<RoomInfoResponse> makeRoomsInfo(
		String workspaceId,
		Page<Room> roomPage
	) {
		// Make uuid set
		Set<String> memberUserIdsOfRooms = new HashSet<>();
		for (Room room : roomPage) {
			memberUserIdsOfRooms.addAll(room.getMembers().stream()
				.map(Member::getUuid)
				.collect(Collectors.toSet()));
		}

		// Receive User list from Workspace
		ApiResponse<WorkspaceMemberInfoListResponse> memberInfo = workspaceRestService.getWorkspaceMembersExcludeUserIds(
			workspaceId,
			memberUserIdsOfRooms.toArray(new String[0])
		);

		// Make Response data
		List<RoomInfoResponse> roomInfoResponses = new ArrayList<>();
		for (Room room : roomPage.getContent()) {
			RoomInfoResponse roomInfoResponse = roomInfoMapper.toDto(room);
			roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

			// Mapping Member List Data to Member Information List
			List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
				.map(memberMapper::toDto)
				.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(memberInfo.getData().getMemberInfoList())) {
				mapperWorkspaceMemberToMember(memberInfo, memberInfoList);
			}
			roomInfoResponse.setMemberList(setLeader(memberInfoList));
			roomInfoResponses.add(roomInfoResponse);
		}
		return roomInfoResponses;
	}

	public AccessType loadAccessType(String workspaceId, String uuid) {
		AccessType result;
		try {
			AccessStatus accessStatus = accessStatusService.getAccessStatus(workspaceId, uuid);
			if (ObjectUtils.isEmpty(accessStatus) || accessStatus.getAccessType() == AccessType.LOGOUT) {
				result = AccessType.LOGOUT;
			} else {
				result = accessStatus.getAccessType();
			}
		} catch (Exception e) {
			log.info("SET MEMBER STATUS EXCEPTION => [{}], [{}]", uuid, e.getMessage());
			result = AccessType.LOGOUT;
		}
		return result;
	}
}