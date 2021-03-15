package com.virnect.serviceserver.serviceremote.application;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.session.SessionType;
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
import com.virnect.serviceserver.serviceremote.dto.request.room.RoomRequest;
import com.virnect.serviceserver.serviceremote.dto.response.CoturnResponse;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.InviteRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.KickRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

	private static final String TAG = SessionRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/room";

	private final RoomRepository roomRepository;
	private final SessionDataRepository sessionDataRepository;
	private final ServiceSessionManager serviceSessionManager;

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

		//companyCode = checkCompanyCode(companyCode);

		ApiResponse<RoomResponse> responseData;

		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
			);
		}

		if (roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType()
			.equals(SessionType.PUBLIC)) {
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
				sessionId, roomRequest, licenseItem, roomRequest.getLeaderId(), sessionJson.toString(),
				tokenResult.toString());
		} else {
			responseData = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_CREATE_FAIL
			);
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

		//companyCode = checkCompanyCode(companyCode);

		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
			);
		}

		if (roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType()
			.equals(SessionType.PUBLIC)) {
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
			responseData = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_CREATE_FAIL
			);
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
			new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
			);
		}

		if (
			roomRequest.getSessionType().equals(SessionType.PRIVATE)
				|| roomRequest.getSessionType().equals(SessionType.PUBLIC))
		{
			// check room request member count is over
			assert licenseItem != null;
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

				CoturnResponse coturnResponse = setCoturnResponse(responseData.getData().getSessionType());
				responseData.getData().getCoturn().add(coturnResponse);

			} else {
				responseData = new ApiResponse<>(
					new RoomResponse(),
					ErrorCode.ERR_ROOM_MEMBER_IS_OVER
				);
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

			CoturnResponse coturnResponse = setCoturnResponse(responseData.getData().getSessionType());
			responseData.getData().getCoturn().add(coturnResponse);

		} else {
			responseData = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_CREATE_FAIL
			);
		}
		return responseData;
	}

	public ApiResponse<RoomResponse> initRoom(
		RoomRequest roomRequest,
		int companyCode
	) {

		//companyCode = checkCompanyCode(companyCode);

		ApiResponse<RoomResponse> responseData;

		// check license item using company code if not virnect
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
			);
		}

		if (roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType()
			.equals(SessionType.PUBLIC)) {
			// check room request member count is over
			assert licenseItem != null;
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

				CoturnResponse coturnResponse = setCoturnResponse(responseData.getData().getSessionType());
				responseData.getData().getCoturn().add(coturnResponse);

			} else {
				responseData = new ApiResponse<>(
					new RoomResponse(),
					ErrorCode.ERR_ROOM_MEMBER_IS_OVER
				);
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

			CoturnResponse coturnResponse = setCoturnResponse(responseData.getData().getSessionType());
			responseData.getData().getCoturn().add(coturnResponse);

		} else {
			responseData = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_CREATE_FAIL
			);
		}
		return responseData;
	}

	public ApiResponse<RoomResponse> joinRoomById(
		String workspaceId,
		String sessionId,
		JoinRoomRequest joinRoomRequest
	) {

		ApiResponse<RoomResponse> responseData;

		ApiResponse<Boolean> dataProcess = this.sessionDataRepository.prepareJoinRoom(
			workspaceId, sessionId, joinRoomRequest.getUuid());

		if (dataProcess.getData()) {
			// generate session id and token
			JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
			JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

			responseData = this.sessionDataRepository.joinRoom(
				workspaceId, sessionId, tokenResult.toString(), joinRoomRequest);

			CoturnResponse coturnResponse = setCoturnResponse(responseData.getData().getSessionType());
			responseData.getData().getCoturn().add(coturnResponse);

		} else {
			LogMessage.formedInfo(
				TAG,
				"REST API: POST " + REST_PATH + "/",
				"joinRoomById",
				"process data get false",
				dataProcess.getMessage()
			);

			responseData = new ApiResponse<>(new RoomResponse());
			responseData.setCode(dataProcess.getCode());
			responseData.setMessage(dataProcess.getMessage());
		}

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
			resultResponse = new ApiResponse<>(new ResultResponse());
			resultResponse.setCode(response.getCode());
			resultResponse.setMessage(response.getMessage());
		}
		return resultResponse;
	}

	public ApiResponse<ResultResponse> kickOutMember(
		String workspaceId,
		String sessionId,
		KickRoomRequest kickRoomRequest
	) {
		ApiResponse<KickRoomResponse> apiResponse = this.sessionDataRepository.kickFromRoom(
			workspaceId, sessionId, kickRoomRequest);
		ApiResponse<ResultResponse> resultResponse;
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
		ResultResponse response = new ResultResponse();
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
			response.setResult(true);
		}
		apiResponse.setData(response);
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
					coturnResponse.setUrl(coturnUrl.replaceAll("\"", ""));
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

}
