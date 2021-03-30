package com.virnect.serviceserver.serviceremote.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.serviceremote.api.SessionRestController;
import com.virnect.serviceserver.serviceremote.dao.SessionDataRepository;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseItem;
import com.virnect.serviceserver.serviceremote.dto.constraint.PushConstants;
import com.virnect.serviceserver.serviceremote.dto.push.SendSignalRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.InviteRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.JoinRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.KickRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.RoomRequest;
import com.virnect.serviceserver.serviceremote.dto.response.CoturnResponse;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.InviteRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.KickRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomDeleteResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomDetailInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

	private static final String TAG = SessionRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/room";

	private final ModelMapper modelMapper;

	private final RoomRepository roomRepository;
	private final SessionDataRepository sessionDataRepository;
	private final RoomHistoryRepository roomHistoryRepository;

	private final SessionTransactionalService sessionService;
	private final HistoryService historyService;
	private final WorkspaceRestService workspaceRestService;
	private final MemberRepository memberRepository;
	private final ServiceSessionManager serviceSessionManager;
	private final FileService fileService;

	private final RemoteServiceConfig config;

	private boolean IsValidUserCapacity(RoomRequest roomRequest, LicenseItem licenseItem) {
		// check room request member count is over
		return roomRequest.getParticipantIds().size() + 1 <= licenseItem.getUserCapacity();
	}

	public ApiResponse<RoomResponse> redialRoomRequest(
		RoomRequest roomRequest,
		String sessionId,
		int companyCode
	) {
		ApiResponse<RoomResponse> responseData;

		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		} else {
			if (roomRequest.getSessionType().equals(SessionType.PRIVATE)
				|| roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
				// check room request member count is over
				assert licenseItem != null;
				if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
					new ApiResponse<>(
						new RoomResponse(),
						ErrorCode.ERR_ROOM_MEMBER_IS_OVER
					);
				}
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				responseData = this.sessionDataRepository.generateRoom(
					sessionId,
					roomRequest,
					licenseItem,
					roomRequest.getLeaderId(),
					sessionJson.toString(),
					tokenResult.toString()
				);

			} else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
				//open session is not need to check member count.
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				// create room
				responseData = this.sessionDataRepository.generateRoom(
					sessionId,
					roomRequest,
					licenseItem,
					roomRequest.getLeaderId(),
					sessionJson.toString(),
					tokenResult.toString()
				);
			} else {
				responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_CREATE_FAIL
				);
			}
		}
		return responseData;
	}

	public ApiResponse<RoomResponse> redialRoomRequestByUserId(
		String client,
		String userId,
		RoomRequest roomRequest,
		String sessionId,
		int companyCode
	) {
		ApiResponse<RoomResponse> responseData;

		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		} else {
			if (roomRequest.getSessionType().equals(SessionType.PRIVATE)
				|| roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
				// check room request member count is over
				if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
					new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
				}
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				// create room
				responseData = this.sessionDataRepository.generateRoom(
					sessionId,
					roomRequest,
					licenseItem,
					userId,
					sessionJson.toString(),
					tokenResult.toString()
				);
			} else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
				//open session is not need to check member count.
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				// create room
				responseData = this.sessionDataRepository.generateRoom(
					sessionId,
					roomRequest,
					licenseItem,
					userId,
					sessionJson.toString(),
					tokenResult.toString()
				);
			} else {
				responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_CREATE_FAIL);
			}
		}
		return responseData;
	}

	public ApiResponse<RoomResponse> initRoomByClient(
		String client,
		String userId,
		RoomRequest roomRequest,
		int companyCode
	) {

		//companyCode = checkCompanyCode(companyCode);

		ApiResponse<RoomResponse> responseData;
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);

		if (licenseItem == null) {
			responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		}
		else {
			if (roomRequest.getSessionType().equals(SessionType.PRIVATE)
				|| roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
				// check room request member count is over
				if (IsValidUserCapacity(roomRequest, licenseItem)) {
					// generate session id and token
					JsonObject sessionJson = serviceSessionManager.generateSession();
					JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

					// create room
					responseData = this.sessionDataRepository.generateRoom(
						roomRequest,
						licenseItem,
						userId,
						sessionJson.toString(),
						tokenResult.toString()
					);
					responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
				} else {
					responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
				}
			} else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
				//open session is not need to check member count.
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				// create room
				responseData = this.sessionDataRepository.generateRoom(
					roomRequest,
					licenseItem,
					userId,
					sessionJson.toString(),
					tokenResult.toString()
				);
				responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
			} else {
				responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_CREATE_FAIL);
			}
		}
		return responseData;
	}

	public ApiResponse<RoomResponse> initRoom(
		RoomRequest roomRequest,
		int companyCode
	) {
		ApiResponse<RoomResponse> responseData;
		// check license item using company code if not virnect
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE);
		} else {
			if (roomRequest.getSessionType().equals(SessionType.PRIVATE)
				|| roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
				// check room request member count is over
				if (IsValidUserCapacity(roomRequest, licenseItem)) {
					// generate session id and token
					JsonObject sessionJson = serviceSessionManager.generateSession();
					JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

					responseData = this.sessionDataRepository.generateRoom(
						roomRequest,
						licenseItem,
						roomRequest.getLeaderId(),
						sessionJson.toString(),
						tokenResult.toString()
					);
					responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
				} else {
					responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_MEMBER_IS_OVER );
				}
			} else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
				// open session is not need to check member count.
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				// create room
				responseData = this.sessionDataRepository.generateRoom(
					roomRequest,
					licenseItem,
					roomRequest.getLeaderId(),
					sessionJson.toString(),
					tokenResult.toString()
				);
				responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
			} else {
				responseData = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_CREATE_FAIL);
			}
		}

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

		ApiResponse<RoomResponse> responseData;

		ApiResponse<Boolean> dataProcess = this.sessionDataRepository.prepareJoinRoom(
			workspaceId, sessionId, joinRoomRequest.getUuid());

		if (dataProcess.getData()) {
			// generate session id and token
			JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
			JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

			responseData = this.sessionDataRepository.joinRoom(
				workspaceId, sessionId, tokenResult.toString(), joinRoomRequest);

			responseData.getData().getCoturn().add(setCoturnResponse(responseData.getData().getSessionType()));
		} else {
			LogMessage.formedInfo(
				TAG,
				"REST API: POST " + REST_PATH + "/",
				"joinRoomById",
				"process data get false",
				dataProcess.getMessage()
			);
			responseData = new ApiResponse<>(new RoomResponse(), dataProcess.getCode(), dataProcess.getMessage());
			/*responseData.setCode(dataProcess.getCode());
			responseData.setMessage(dataProcess.getMessage());*/
		}

		/*
		 * 1. UNLOAD 상태 체크
		 * 2. 발행한 토큰 파기, LEAVE ROOM  (자동 처리 확인)
		 * 3. DB DATA UPDATE
		 * 4. RESPONSE ERROR MESSAGE
		 */

		return responseData;
	}

	public ApiResponse<ResultResponse> exitRoomBySessionIdAndUserId(
		String workspaceId,
		String sessionId,
		String userId
	) {

		ApiResponse<ResultResponse> responseData;

		Member member = null;
		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);

		if (room == null) {
			responseData = new ApiResponse<>(new ResultResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
		} else {
			for (Member participant : room.getMembers()) {
				if (participant.getUuid().equals(userId)) {
					member = participant;
				}
			}
			if (member == null) {
				responseData = new ApiResponse<>(new ResultResponse(), ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
			} else {
				ErrorCode errorCode;

				if (member.getMemberType().equals(MemberType.LEADER)) {
					errorCode = ErrorCode.ERR_ROOM_LEADER_INVALID_EXIT;
				} else if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
					errorCode = ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
				} else {
					errorCode = ErrorCode.ERR_SUCCESS;
				}

				if (errorCode.equals(ErrorCode.ERR_SUCCESS)) {

					room.getMembers().remove(member);
					roomRepository.save(room);

					ResultResponse resultResponse = new ResultResponse();
					resultResponse.setUserId(userId);
					resultResponse.setResult(true);
					responseData = new ApiResponse<>(resultResponse);
				} else {
					responseData = new ApiResponse<>(new ResultResponse(), errorCode);
				}
			}
		}
		return responseData;
	}

	public ApiResponse<ResultResponse> inviteMember(
		String workspaceId,
		String sessionId,
		InviteRoomRequest inviteRoomRequest
	) {
		ApiResponse<InviteRoomResponse> response = this.sessionDataRepository.inviteMember(
			workspaceId, sessionId, inviteRoomRequest);
		ApiResponse<ResultResponse> resultResponse;
		if (response.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
			//send push message
			this.sessionDataRepository.sendInviteMessage(response.getData());

			resultResponse = new ApiResponse<>(
				new ResultResponse(inviteRoomRequest.getLeaderId(), true, LocalDateTime.now(), new HashMap<>())
			);
		} else {
			resultResponse = new ApiResponse<>(new ResultResponse(), response.getCode(), response.getMessage());
		}
		return resultResponse;
	}

	public ApiResponse<ResultResponse> kickOutMember(
		String workspaceId,
		String sessionId,
		KickRoomRequest kickRoomRequest
	) {
		ApiResponse<ResultResponse> resultResponse;

		ApiResponse<KickRoomResponse> apiResponse = this.sessionDataRepository.kickFromRoom(
			workspaceId, sessionId, kickRoomRequest);

		if (apiResponse.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
			String connectionId = apiResponse.getData().getConnectionId();
			if (connectionId == null || connectionId.isEmpty()) {
				//send push message
				this.sessionDataRepository.sendEvictMessage(apiResponse.getData());
				resultResponse = new ApiResponse<>(new ResultResponse(
					kickRoomRequest.getLeaderId(), true, LocalDateTime.now(), new HashMap<>()
				));
			} else {
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
					resultResponse = new ApiResponse<>(new ResultResponse());
					resultResponse.setCode(Integer.parseInt(jsonObject.get("status").getAsString()));
					resultResponse.setMessage(jsonObject.get("message").getAsString());
				} else {
					//send force disconnected
					//todo:forceResult when get false do process something.
					boolean forceResult = serviceSessionManager.evictParticipant(sessionId, connectionId);
					log.info("evictParticipant :{}", forceResult);
					resultResponse = new ApiResponse<>(new ResultResponse(
						kickRoomRequest.getLeaderId(), true, LocalDateTime.now(), new HashMap<>()
					));
				}
			}
		} else {
			resultResponse = new ApiResponse<>(new ResultResponse());
			resultResponse.setCode(apiResponse.getCode());
			resultResponse.setMessage(apiResponse.getMessage());
		}
		return resultResponse;
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
						coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
						coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
						coturnResponse.setUrl(coturnUrl);
					}
				} else {
					for (String coturnUrl : urlList) {
						coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
						coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
						coturnResponse.setUrl(coturnUrl);
					}
				}
			}
			break;
			case PUBLIC:
			case PRIVATE: {
				for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
					coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
					coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
					coturnResponse.setUrl(coturnUrl);
				}
			}
			break;
		}
		return coturnResponse;
	}

	private int checkCompanyCode(int companyCode) {
		if (config.getProfile().equals("local") || config.getProfile().equals("develop")) {
			companyCode = 0;
		}
		return companyCode;
	}

	/**
	 * todo: need to change this process to batch process
	 */
	//public DataProcess<Void> removeAllRoom() {
	//return new RepoDecoder<List<Room>, Void>(RepoDecoderType.DELETE) {
	public void removeAllRoom() {
		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"removeAllRoom",
			"the server restarts and deletes the room list information"
		);
		List<Room> roomList = sessionService.getRoomList();
		for (Room room : roomList) {

			// Remote Room History Entity Create
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
				sessionService.deleteMember(roomMember);
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
			sessionService.setRoomHistory(roomHistory);

			sessionService.deleteRoom(room);
		}
	}

	public ApiResponse<RoomDetailInfoResponse> getRoomDetailBySessionId(String workspaceId, String sessionId) {

		LogMessage.formedInfo(
			TAG,
			"invokeDataProcess",
			"loadRoom",
			"room info retrieve by session id",
			sessionId
		);

		ApiResponse<RoomDetailInfoResponse> responseData;

		List<MemberInfoResponse> memberInfoList;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if(room == null) {
			return new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
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

				memberInfoList.sort((t1, t2) -> {
					if (t1.getMemberType().equals(MemberType.LEADER)) {
						return 1;
					}
					return 0;
				});

				// Set Member List to Room Detail Information Response
				roomDetailInfoResponse.setMemberList(memberInfoList);
				responseData = new ApiResponse<>(roomDetailInfoResponse);
			}
		}
		return responseData;
	}

	public RoomInfoListResponse getRoomList(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {
		//Page<Room> roomPage = roomRepository.findAll(joinMember(workspaceId, userId), pageable);

		Page<Room> roomPage = roomRepository.findRoomByWorkspaceIdAndUserId(workspaceId, userId, paging, pageable);

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
				//.filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
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
			roomInfoResponse.setMemberList(
				setLeader(memberInfoList)
			);
			roomInfoList.add(roomInfoResponse);
		}

		// Evicted and Unload check
		CheckEvictedAndUnload(userId, roomInfoList);

		if (paging) {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.numberOfElements(roomPage.getNumberOfElements())
				.totalPage(roomPage.getTotalPages())
				//.totalElements(roomPage.getTotalElements())
				.totalElements(roomInfoList.size())
				.last(roomPage.isLast())
				.build();
		} else {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(0)
				.currentSize(0)
				.numberOfElements(roomPage.getNumberOfElements())
				.totalPage(1)
				//.totalElements(roomPage.getTotalElements())
				.totalElements(roomInfoList.size())
				.last(true)
				.build();
		}
		return new RoomInfoListResponse(roomInfoList, pageMeta);
	}

	private void CheckEvictedAndUnload(String userId, @NotNull List<RoomInfoResponse> roomInfoList) {
		for (Iterator<RoomInfoResponse> roomInfoResponseIterator = roomInfoList.iterator(); roomInfoResponseIterator.hasNext();) {

			List<MemberInfoResponse> memberInfoResponses = roomInfoResponseIterator.next().getMemberList();

			for (MemberInfoResponse memberInfoResponse : memberInfoResponses) {
				if (memberInfoResponse.getUuid().equals(userId)) {
					if (memberInfoResponse.getMemberStatus() == MemberStatus.EVICTED || memberInfoResponse.getMemberStatus() == MemberStatus.UNLOAD) {
						roomInfoResponseIterator.remove();
					}
				}
			}

			/*boolean evictedCheck = false;
			boolean[] unloadCheck = new boolean[memberInfoResponses.size()];
			boolean unloadCheckResult = true;

			for (int i = 0 ; i < memberInfoResponses.size() ; i++) {
				evictedCheck = memberInfoResponses.get(i).getUuid().equals(userId) && memberInfoResponses.get(i).getMemberStatus().equals(MemberStatus.EVICTED);
				if (memberInfoResponses.get(i).getMemberStatus().equals(MemberStatus.UNLOAD)) {
					unloadCheck[i] = true;
				} else {
					unloadCheck[i] = false;
				}
			}

			for (boolean unload : unloadCheck)	{
				unloadCheckResult = unloadCheckResult && unload;
			}
			if (evictedCheck || unloadCheckResult) {
				roomInfoResponseIterator.remove();
			}*/
		}
	}

	private List<MemberInfoResponse> setLeader(List<MemberInfoResponse> members) {
		members.sort((t1, t2) -> {
			int index1 = t1.getMemberType().ordinal();
			int index2 = t2.getMemberType().ordinal();
			return Integer.compare(index1, index2);
		});
		return members;
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

		List<String> userIds = new ArrayList<>();
		for (MemberInfoResponse memberInfo : memberInfoList) {
			if (memberInfo.getUuid() == null || memberInfo.getUuid().isEmpty()) {
				//if memberInfo is empty
				log.info("loadFromDatabase::searchRoomPageList:: some member dose not have uuid");
			} else {
				userIds.add(memberInfo.getUuid());
			}
		}

		Page<Room> roomPage = roomRepository.findRoomBySearch(workspaceId, userId, userIds, search, pageable);

		/*if (userIds.isEmpty()) {
			log.info(
				"loadFromDatabase::searchRoomPageList::memberInfoList is empty can not find, search with room title");
			roomPage = roomRepository.findAll(joinMember(workspaceId, userId, search), pageable);
		} else {
			log.info("loadFromDatabase::searchRoomPageList::memberInfoList is not empty");
			roomPage = roomRepository.findAll(joinMember(workspaceId, userId, userIds, search), pageable);
		}*/
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

			roomInfoResponse.setMemberList(setLeader(memberInfos));
			roomInfoList.add(roomInfoResponse);
		}

		// Evicted and Unload check
		CheckEvictedAndUnload(userId, roomInfoList);

		// Page Metadata
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(roomPage.getNumberOfElements())
			.totalPage(roomPage.getTotalPages())
			.totalElements(roomInfoList.size())
			.last(roomPage.isLast())
			.build();

		return new RoomInfoListResponse(roomInfoList, pageMeta);
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

		ApiResponse<RoomDetailInfoResponse> responseData;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);

		List<MemberInfoResponse> memberInfoList;
		if (!ObjectUtils.isEmpty(room)) {
			if (room.getLeaderId().equals(modifyRoomInfoRequest.getUuid())) {

				room.setTitle(modifyRoomInfoRequest.getTitle());
				room.setDescription(modifyRoomInfoRequest.getDescription());

				Room updatedRoom = roomRepository.save(room);

				// mapping data
				RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(updatedRoom, RoomDetailInfoResponse.class);
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

	public ApiResponse<RoomDeleteResponse> deleteRoomById(String workspaceId, String sessionId, String userId) {

		ApiResponse<RoomDeleteResponse> responseData;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);

		if (!ObjectUtils.isEmpty(room)) {
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
			responseData = new ApiResponse<>(new RoomDeleteResponse(sessionId, true, LocalDateTime.now()));
		} else {
			responseData = new ApiResponse<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		return responseData;
	}

}
