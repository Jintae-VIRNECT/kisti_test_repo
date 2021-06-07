package com.virnect.serviceserver.serviceremote.dao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.client.RemoteServiceException;
import com.virnect.data.application.record.RecordRestService;
import com.virnect.data.application.user.UserRestService;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.domain.DeviceType;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.domain.session.SessionProperty;
import com.virnect.data.domain.session.SessionPropertyHistory;
import com.virnect.data.domain.session.SessionType;
import com.virnect.data.dto.rest.PushResponse;
import com.virnect.data.dto.rest.StopRecordingResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.mediaserver.core.EndReason;
import com.virnect.mediaserver.core.Participant;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.serviceremote.application.HistoryService;
import com.virnect.serviceserver.serviceremote.application.PushMessageClient;
import com.virnect.serviceserver.serviceremote.application.SessionTransactionalService;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseItem;
import com.virnect.serviceserver.serviceremote.dto.constraint.PushConstants;
import com.virnect.serviceserver.serviceremote.dto.request.room.InviteRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.JoinRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.KickRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.RoomRequest;
import com.virnect.serviceserver.serviceremote.dto.response.room.InviteRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.KickRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.rpc.ClientMetaData;
import com.virnect.serviceserver.serviceremote.dto.response.session.SessionResponse;
import com.virnect.serviceserver.serviceremote.dto.response.session.SessionTokenResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionDataRepository {

    public static final String WS_PATH = "/remote/websocket";

    private static final String TAG = SessionDataRepository.class.getSimpleName();
    private final int ROOM_MEMBER_LIMIT = 30;

    private final ObjectMapper objectMapper;

    private final SessionTransactionalService sessionService;
    private final HistoryService historyService;
    private final PushMessageClient pushMessageClient;
    private final UserRestService userRestService;
    private final RecordRestService recordRestService;

    private final AccessStatusService accessStatusService;

    private final RemoteServiceConfig config;

    private final MemberRepository memberRepository;

    public void setAccessStatus(Participant participant, AccessType accessType) {
        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ClientMetaData clientMetaData = null;
        try {
            String workspaceId;
            String userId;
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
            userId = clientMetaData.getClientData();
            Member member = memberRepository.findByUuid(userId).orElse(null);
            if (member != null) {
                workspaceId = member.getWorkspaceId();
                accessStatusService.saveAccessStatus(workspaceId + "_" + userId, accessType);
            } else {
                log.info("member is null in setAccessStatus :: userId : {}", clientMetaData.getClientData());
            }
        } catch (JsonProcessingException e) {
            log.info("setAccessStatus error :: userId : {}", clientMetaData.getClientData());
        }
    }

    public Boolean destroySession(String sessionId, EndReason reason) {

        Room room = sessionService.getRoom(sessionId);

        if (ObjectUtils.isEmpty(room)) {
            LogMessage.formedError(
                TAG,
                "DESTROY SESSION EVENT",
                "destroySession",
                reason.toString(),
                ErrorCode.ERR_ROOM_NOT_FOUND.getMessage()
            );
            return false;
        }

        LogMessage.formedInfo(
            TAG,
            "DESTROY SESSION EVENT",
            "destroySession",
            reason.toString(),
            sessionId
        );

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
        return true;
    }

    public Boolean stopRecordSession(String sessionId) {

        LogMessage.formedInfo(
            TAG,
            "stopRecordSession",
            "invokeDataProcess",
            "destroy session",
            sessionId
        );

        Room room = sessionService.getRoom(sessionId);
        if (ObjectUtils.isEmpty(room)) {
            return false;
        }

        ApiResponse<StopRecordingResponse> apiResponse = recordRestService.stopRecordingBySessionId(
            room.getWorkspaceId(), room.getLeaderId(), room.getSessionId());
        if (apiResponse.getCode() == 200) {
            if (apiResponse.getData() != null) {
                for (String recordingId : apiResponse.getData().getRecordingIds()) {
                    LogMessage.formedInfo(
                        TAG,
                        "stopRecordSession",
                        "invokeDataProcess",
                        "recording id response",
                        recordingId
                    );
                }
            }
        } else {
            LogMessage.formedInfo(
                TAG,
                "stopRecordSession",
                "invokeDataProcess",
                "recording api response error code",
                String.valueOf(apiResponse.getCode())
            );
        }
        return true;
    }

    public Boolean leaveSession(Participant participant, String sessionId, EndReason reason) {

        ClientMetaData clientMetaData = null;

        // preDataProcess
        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        // Load DB Data
        Room room = sessionService.getRoom(sessionId);
        if (ObjectUtils.isEmpty(room)) {
            LogMessage.formedError(
                TAG,
                "LEAVE SESSION EVENT",
                "leaveSession",
                reason.toString(),
                ErrorCode.ERR_ROOM_NOT_FOUND.getMessage()
            );
            throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        for (Member member : room.getMembers()) {
            if (member.getUuid().equals(clientMetaData.getClientData())) {
                //set status unload
                member.setMemberStatus(MemberStatus.UNLOAD);
                //set connection id to empty
                member.setConnectionId("");
                //set end time
                LocalDateTime endTime = LocalDateTime.now();
                member.setEndDate(endTime);

                //time diff seconds
                Long totalDuration = member.getDurationSec();
                Duration duration = Duration.between(member.getStartDate(), endTime);
                member.setDurationSec(totalDuration + duration.getSeconds());

                //save member
                sessionService.setMember(member);
            }
        }
        return true;
    }

    public Boolean closeSession(Participant participant, String sessionId, EndReason reason) {

        ClientMetaData clientMetaData = null;
        Room room = sessionService.getRoom(sessionId);

        // pre data process
        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        if (room == null) {
            LogMessage.formedInfo(
                TAG,
                "LEAVE SESSION EVENT",
                "leaveSession",
                reason.toString()
            );
            return true;
        }

        //do update room data
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

        return false;
    }

    public ErrorCode joinSession(Participant participant, String sessionId) {

        ClientMetaData clientMetaData;
        Room room = sessionService.getRoom(sessionId);
        if (room == null) {
            return ErrorCode.ERR_ROOM_NOT_FOUND;
        }

        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        clientMetaData = null;
        try {
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //assert clientMetaData != null;

        log.info("session join and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session join and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session join and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        Member member = sessionService.getMember(
            room.getWorkspaceId(),
            sessionId,
            clientMetaData.getClientData()
        );

        if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
            return ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID; //Code.EXISTING_USER_IN_ROOM_ERROR_CODE
        }

        try {
            member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
            member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
            member.setConnectionId(participant.getParticipantPublicId());
            member.setMemberStatus(MemberStatus.LOAD);
            sessionService.setMember(member);
        } catch (NullPointerException exception) {
            LogMessage.formedError(
                TAG,
                "JOIN SESSION EVENT",
                "joinSession",
                exception.getClass().getName(),
                exception.getMessage()
            );
            throw new RemoteServiceException(
                RemoteServiceException.Code.USER_METADATA_FORMAT_INVALID_ERROR_CODE,
                "Invalid metadata lacking parameter"
            );
        }
        return ErrorCode.ERR_SUCCESS;
    }

    public Boolean disconnectSession(Participant participant, String sessionId, EndReason reason) {

        // Pre data process
        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ClientMetaData clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);

            if (clientMetaData == null) {
                throw new RestServiceException(ErrorCode.ERR_SESSION_CLIENT_METADATA_NULL);
            }

            log.info("session disconnect and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
            log.info("session disconnect and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
            log.info("session disconnect and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

            // Load DB Data
            Room room = sessionService.getRoom(sessionId);
            if (room == null) {
                throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
            }

            for (Member member : room.getMembers()) {
                if (member.getUuid().equals(clientMetaData.getClientData())) {
                    log.info("session disconnect and sessionEventHandler evict user id::{}", member.getUuid());
                    //set status evicted
                    member.setMemberStatus(MemberStatus.EVICTED);
                    //set connection id to empty
                    member.setConnectionId("");
                    //set end time
                    LocalDateTime endTime = LocalDateTime.now();
                    member.setEndDate(endTime);

                    //time diff seconds
                    Long totalDuration = member.getDurationSec();
                    Duration duration = Duration.between(member.getStartDate(), endTime);
                    member.setDurationSec(totalDuration + duration.getSeconds());

                    //set room null
                    //member.setRoom(null);

                    //save member
                    sessionService.setMember(member);
                }
            }
        } catch (JsonProcessingException e) {
           throw new RestServiceException(ErrorCode.ERR_SESSION_CLIENT_METADATA_EXCEPTION);
        }
        return true;
    }

    public Boolean generateRoomSession(String sessionId) {
        log.info("GENERATE ROOM SESSION RETRIEVE BY SESSION ID => [{}]", sessionId);
        sessionService.createSession(sessionId);
        return true;
    }

    public UserInfoResponse checkUserValidation(String userId) {

        ApiResponse<UserInfoResponse> feignResponse = userRestService.getUserInfoByUserId(userId);
        //todo:check something?

        return feignResponse.getData();
    }

    public PushResponse sendSessionCreate(String sessionId) {

        Room room = sessionService.getRoom(sessionId);
        if (room == null) {
            throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        SessionProperty sessionProperty = room.getSessionProperty();
        String userId = sessionProperty.getPublisherId();
        List<String> targetIds = room.getMembers().stream()
            .map(Member::getUuid)
            .filter(s -> !s.equals(userId))
            .collect(Collectors.toList());

        log.info("sendSessionCreate:: userId : {}", userId);

        for (String id : targetIds) {
            log.info("sendSessionCreate:: targetIds : {}", id);
        }

        //send push message invite
        pushMessageClient.setPush(
            PushConstants.PUSH_SERVICE_REMOTE,
            PushConstants.SEND_PUSH_ROOM_INVITE,
            room.getWorkspaceId(),
            sessionProperty.getPublisherId(),
            targetIds
        );

        UserInfoResponse userInfoResponse = checkUserValidation(userId);
        ApiResponse<PushResponse> pushResponse = pushMessageClient.sendPushInvite(
            room.getSessionId(),
            room.getTitle(),
            userInfoResponse.getNickname(),
            userInfoResponse.getProfile(),
            room.getLeaderId()
        );

        if (pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            log.info("push send message executed but not success");
            log.info("push response: [code] {}", pushResponse.getCode());
            log.info("push response: [message] {}", pushResponse.getMessage());
        } else {
            log.info("push send message executed success {}", pushResponse.toString());
        }

        return pushResponse.getData();
    }

    public PushResponse sendInviteMessage(InviteRoomResponse inviteRoomResponse) {

        UserInfoResponse userInfoResponse = checkUserValidation(inviteRoomResponse.getLeaderId());

        pushMessageClient.setPush(
            PushConstants.PUSH_SERVICE_REMOTE,
            PushConstants.SEND_PUSH_ROOM_INVITE,
            inviteRoomResponse.getWorkspaceId(),
            inviteRoomResponse.getLeaderId(),
            inviteRoomResponse.getParticipantIds());

        //set push message invite room contents
        ApiResponse<PushResponse> pushResponse = pushMessageClient.sendPushInvite(
            inviteRoomResponse.getSessionId(),
            inviteRoomResponse.getTitle(),
            userInfoResponse.getNickname(),
            userInfoResponse.getProfile(),
            inviteRoomResponse.getLeaderId()
        );

        if (pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            log.info("push send message executed but not success");
            log.info("push response: [code] {}", pushResponse.getCode());
            log.info("push response: [message] {}", pushResponse.getMessage());
        } else {
            log.info("push send message executed success {}", pushResponse.toString());
        }

        return pushResponse.getData();
    }

    public PushResponse sendEvictMessage(KickRoomResponse kickRoomResponse) {

        //if connection id cannot find, push message and just remove user
        pushMessageClient.setPush(
            PushConstants.PUSH_SERVICE_REMOTE,
            PushConstants.SEND_PUSH_ROOM_EVICT,
            kickRoomResponse.getWorkspaceId(),
            kickRoomResponse.getLeaderId(),
            Arrays.asList(kickRoomResponse.getParticipantId()));

        ApiResponse<PushResponse> pushResponse = pushMessageClient.sendPushEvict();
        if (pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            log.info("push send message executed but not success");
            log.info("push response: [code] {}", pushResponse.getCode());
            log.info("push response: [message] {}", pushResponse.getMessage());
        } else {
            log.info("push send message executed success {}", pushResponse.toString());
        }
        return pushResponse.getData();
    }

    public ApiResponse<RoomResponse> generateRoom(
        RoomRequest roomRequest,
        LicenseItem licenseItem,
        String publisherId,
        String session,
        String sessionToken
    ) {
        SessionResponse sessionResponse = null;
        SessionTokenResponse sessionTokenResponse = null;

        // Prepare data process
        try {
            sessionResponse = objectMapper.readValue(session, SessionResponse.class);
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Set data
        // Remote Room Entity Create
        Room room = Room.builder()
            .sessionId(sessionResponse.getId())
            .title(roomRequest.getTitle())
            .description(roomRequest.getDescription())
            .leaderId(roomRequest.getLeaderId())
            .workspaceId(roomRequest.getWorkspaceId())
            .maxUserCount(licenseItem.getUserCapacity())
            .licenseName(licenseItem.name())
            .videoRestrictedMode(roomRequest.isVideoRestrictedMode())
            .audioRestrictedMode(roomRequest.isAudioRestrictedMode())
            .build();

        // Remote Session Property Entity Create
        SessionProperty sessionProperty = SessionProperty.builder()
            .mediaMode("ROUTED")
            .recordingMode("MANUAL")
            .defaultOutputMode("COMPOSED")
            .defaultRecordingLayout("BEST_FIT")
            .recording(true)
            .keepalive(roomRequest.isKeepAlive())
            .sessionType(roomRequest.getSessionType())
            .publisherId(publisherId)
            .room(room)
            .build();

        room.setSessionProperty(sessionProperty);

        if (!roomRequest.getLeaderId().isEmpty()) {
            log.info("leader Id is {}", roomRequest.getLeaderId());
            Member member = Member.builder()
                .room(room)
                .memberType(MemberType.LEADER)
                .workspaceId(roomRequest.getWorkspaceId())
                .uuid(roomRequest.getLeaderId())
                .sessionId(room.getSessionId())
                .build();

            room.getMembers().add(member);
        } else {
            log.info("leader Id is null");
        }

        // Set member
        if (!roomRequest.getParticipantIds().isEmpty()) {
            for (String participant : roomRequest.getParticipantIds()) {
                log.info("getParticipants Id is {}", participant);
                Member member = Member.builder()
                    .room(room)
                    .memberType(MemberType.UNKNOWN)
                    .workspaceId(roomRequest.getWorkspaceId())
                    .uuid(participant)
                    .sessionId(room.getSessionId())
                    .build();

                room.getMembers().add(member);
            }
        } else {
            log.info("participants Id List is null");
        }

        if (ObjectUtils.isEmpty(sessionService.createRoom(room))) {
            return new ApiResponse<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
        }

        RoomResponse roomResponse = new RoomResponse();
        //not set session create at property
        roomResponse.setSessionId(sessionResponse.getId());
        roomResponse.setToken(sessionTokenResponse.getToken());
        roomResponse.setWss(config.remoteServiceProperties.getWss() + WS_PATH);
        roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
        roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());
        roomResponse.setSessionType(room.getSessionProperty().getSessionType());
        return new ApiResponse<>(roomResponse);
    }

    public ApiResponse<RoomResponse> generateRoom(
        String preSessionId,
        RoomRequest roomRequest,
        LicenseItem licenseItem,
        String publisherId,
        String session,
        String sessionToken
    ) {
        SessionResponse sessionResponse = null;
        SessionTokenResponse sessionTokenResponse = null;
        String profile;

        // Prepair data process
        try {
            sessionResponse = objectMapper.readValue(session, SessionResponse.class);
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RoomHistory roomHistory = historyService.getRoomHistory(roomRequest.getWorkspaceId(), preSessionId);
        if (roomHistory == null) {
            return new ApiResponse<>(ErrorCode.ERR_HISTORY_ROOM_NOT_FOUND);
        }

        log.info(
            "REDIAL ROOM::#generateRoom::re-generate room by history::session_id => [{}]", preSessionId);
        profile = roomHistory.getProfile();

        Room room = Room.builder()
            .sessionId(sessionResponse.getId())
            .title(roomRequest.getTitle())
            .description(roomRequest.getDescription())
            .leaderId(roomRequest.getLeaderId())
            .workspaceId(roomRequest.getWorkspaceId())
            .maxUserCount(licenseItem.getUserCapacity())
            .licenseName(licenseItem.name())
            .videoRestrictedMode(roomRequest.isVideoRestrictedMode())
            .audioRestrictedMode(roomRequest.isAudioRestrictedMode())
            .build();

        room.setProfile(profile);

        // Remote Session Property Entity Create
        SessionProperty sessionProperty = SessionProperty.builder()
            .mediaMode("ROUTED")
            .recordingMode("MANUAL")
            .defaultOutputMode("COMPOSED")
            .defaultRecordingLayout("BEST_FIT")
            .recording(true)
            .keepalive(roomRequest.isKeepAlive())
            .sessionType(roomRequest.getSessionType())
            .publisherId(publisherId)
            .room(room)
            .build();

        room.setSessionProperty(sessionProperty);

        // set room members
        if (!roomRequest.getLeaderId().isEmpty()) {
            log.info("leader Id is {}", roomRequest.getLeaderId());
            Member member = Member.builder()
                .room(room)
                .memberType(MemberType.LEADER)
                .workspaceId(roomRequest.getWorkspaceId())
                .uuid(roomRequest.getLeaderId())
                .sessionId(room.getSessionId())
                .build();

            room.getMembers().add(member);
        } else {
            log.info("leader Id is null");
        }

        if (!roomRequest.getParticipantIds().isEmpty()) {
            for (String participant : roomRequest.getParticipantIds()) {
                log.info("getParticipants Id is {}", participant);
                Member member = Member.builder()
                    .room(room)
                    .memberType(MemberType.UNKNOWN)
                    .workspaceId(roomRequest.getWorkspaceId())
                    .uuid(participant)
                    .sessionId(room.getSessionId())
                    .build();

                room.getMembers().add(member);
            }
        } else {
            log.info("participants Id List is null");
        }

        if (ObjectUtils.isEmpty(sessionService.createRoom(room))) {
            return new ApiResponse<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
        }

        RoomResponse roomResponse = new RoomResponse();
        //not set session create at property
        roomResponse.setSessionId(sessionResponse.getId());
        roomResponse.setToken(sessionTokenResponse.getToken());
        roomResponse.setWss(config.remoteServiceProperties.getWss() + WS_PATH);
        roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
        roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());
        roomResponse.setSessionType(room.getSessionProperty().getSessionType());

        return new ApiResponse<>(roomResponse);
    }

    /**
     * Prepare to join the room the user is....
     */
    public ApiResponse<Boolean> prepareJoinRoom(String workspaceId, String sessionId, String userId) {
            
        Room room = sessionService.getRoomForWrite(workspaceId, sessionId);
        if (room == null) {
            return new ApiResponse<>(false, ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        if (!room.getMembers().isEmpty()) {
            long memberCount = room.getMembers().stream().filter(member -> !(member.getMemberStatus() == MemberStatus.UNLOAD)).count();
            if (memberCount >= ROOM_MEMBER_LIMIT) {
                return new ApiResponse<>(false, ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
            }
        }

        Member member = null;
        for (Member m : room.getMembers()) {
            if (m.getUuid().equals(userId)) {
                member = m;
            }
        }

        boolean result = false;
        ErrorCode errorCode = ErrorCode.ERR_SUCCESS;

        switch (room.getSessionProperty().getSessionType()) {
            case PRIVATE:
            case PUBLIC: {
                if (member != null) {
                    MemberStatus memberStatus = member.getMemberStatus();
                    if (memberStatus == MemberStatus.UNLOAD) {
                        member.setMemberStatus(MemberStatus.LOADING);
                        sessionService.setMember(member);
                        result = true;

                        // LOADING일때 ACCESS STATUS JOIN
                        accessStatusService.saveAccessStatus(member.getWorkspaceId() + "_" + member.getUuid(), AccessType.JOIN);

                    } else if (memberStatus == MemberStatus.EVICTED) {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_EVICTED_STATUS;
                    } else if (memberStatus == MemberStatus.LOAD) {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED;
                    } else {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
                    }
                } else {
                    errorCode = ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED;
                }
            }
            break;
            case OPEN: {
                if (member != null) {
                    MemberStatus memberStatus = member.getMemberStatus();
                    if (memberStatus.equals(MemberStatus.UNLOAD) || memberStatus.equals(MemberStatus.EVICTED))
                    {
                        member.setMemberStatus(MemberStatus.LOADING);
                        sessionService.setMember(member);
                        result = true;
                    } else {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
                    }
                } else {
                    Member roomMember = Member.builder()
                        .room(room)
                        .memberType(MemberType.UNKNOWN)
                        .uuid(userId)
                        .workspaceId(workspaceId)
                        .sessionId(sessionId)
                        .build();
                    roomMember.setMemberStatus(MemberStatus.LOADING);
                    room.getMembers().add(roomMember);
                    sessionService.updateRoom(room);
                    result = true;
                }
            }
            break;
            default: {
                result = false;
                errorCode = ErrorCode.ERR_UNSUPPORTED_DATA_TYPE_EXCEPTION;
            }
        }
        return new ApiResponse<>(result, errorCode);
    }

    public ApiResponse<RoomResponse> joinRoom(
        String workspaceId,
        String sessionId,
        String sessionToken,
        JoinRoomRequest joinRoomRequest
    ) {

        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (room == null) {
            return new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        // Pre data process
        SessionTokenResponse sessionTokenResponse = null;
        try {
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ErrorCode errorCode = ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED;
        for (Member member : room.getMembers()) {
            if (member.getUuid().equals(joinRoomRequest.getUuid())) {
                MemberStatus memberStatus = member.getMemberStatus();
                switch (memberStatus) {
                    case LOADING:
                        errorCode = ErrorCode.ERR_SUCCESS;
                        break;
                    case LOAD:
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED;
                        break;
                    case EVICTED:
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
                        break;
                }
            }
        }

        if (errorCode != ErrorCode.ERR_SUCCESS) {
            return new ApiResponse<>(new RoomResponse(), errorCode);
        }

        RoomResponse roomResponse = new RoomResponse();
        //not set session create at property
        roomResponse.setSessionId(sessionId);
        roomResponse.setToken(sessionTokenResponse.getToken());
        roomResponse.setWss(config.remoteServiceProperties.getWss() + WS_PATH);
        roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
        roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());
        roomResponse.setSessionType(room.getSessionProperty().getSessionType());

        return new ApiResponse<>(roomResponse);
    }

    public ApiResponse<KickRoomResponse> kickFromRoom(
        String workspaceId,
        String sessionId,
        KickRoomRequest kickRoomRequest
    ) {

        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (ObjectUtils.isEmpty(room)) {
            return new ApiResponse<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        Member member = null;
        for (Member participant : room.getMembers()) {
            if (participant.getUuid().equals(kickRoomRequest.getParticipantId())) {
                member = participant;
            }
        }
        if (ObjectUtils.isEmpty(member)) {
            return new ApiResponse<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
        }

        if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
            return new ApiResponse<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        }

        KickRoomResponse kickRoomResponse = new KickRoomResponse();
        kickRoomResponse.setWorkspaceId(room.getWorkspaceId());
        kickRoomResponse.setSessionId(room.getSessionId());
        kickRoomResponse.setLeaderId(room.getLeaderId());
        kickRoomResponse.setParticipantId(kickRoomRequest.getParticipantId());
        kickRoomResponse.setConnectionId(member.getConnectionId());

        //if connection id cannot find, push message and just remove user
        if (Strings.isBlank(member.getConnectionId())) {
            sessionService.updateMember(member, MemberStatus.EVICTED);
        }

        return new ApiResponse<>(kickRoomResponse);
    }

    public ApiResponse<InviteRoomResponse> inviteMember(
        String workspaceId, String sessionId, InviteRoomRequest inviteRoomRequest
    ) {
        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (ObjectUtils.isEmpty(room)) {
            return new ApiResponse<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        if (!room.getLeaderId().equals(inviteRoomRequest.getLeaderId())) {
            return new ApiResponse<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        }

        List<Member> members = room.getMembers().stream()
            .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
            .collect(Collectors.toList());

        //check room member is exceeded limitation
        if (members.size() + inviteRoomRequest.getParticipantIds().size() > room.getMaxUserCount()) {
            return new ApiResponse<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
        }

        //check invited member is already joined
        List<String> userIds = members.stream()
            .map(Member::getUuid)
            .collect(Collectors.toList());

        for (String participant : inviteRoomRequest.getParticipantIds()) {
            if (userIds.contains(participant)) {
                return new ApiResponse<>(
                    new InviteRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
            }
        }

        //update room member using iterator avoid to ConcurrentModificationException? ...
        sessionService.updateMember(room, inviteRoomRequest.getParticipantIds());

        InviteRoomResponse response = new InviteRoomResponse();
        response.setWorkspaceId(workspaceId);
        response.setSessionId(sessionId);
        response.setLeaderId(inviteRoomRequest.getLeaderId());
        response.setParticipantIds(inviteRoomRequest.getParticipantIds());
        response.setTitle(room.getTitle());
        return new ApiResponse<>(response);
    }

    /**
     * todo: need to change this process to batch process
     */
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

    public ApiResponse<RoomResponse> joinOpenRoomOnlyNonmember(String workspaceId, String sessionId, String sessionToken) {

        Room room = sessionService.getRoomForWrite(workspaceId, sessionId);
        if (room == null) {
            throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        if (room.getSessionProperty().getSessionType() != SessionType.OPEN) {
            throw new RestServiceException(ErrorCode.ERR_ROOM_INFO_ACCESS);
        }

        if (!room.getMembers().isEmpty()) {
            long memberCount = room.getMembers().stream().filter(member -> !(member.getMemberStatus() == MemberStatus.UNLOAD)).count();
            if (memberCount >= ROOM_MEMBER_LIMIT) {
                throw new RestServiceException(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
            }
        }

        // Pre data process
        SessionTokenResponse sessionTokenResponse = null;
        try {
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Member member = Member.builder()
            .workspaceId(workspaceId)
            .sessionId(sessionId)
            .uuid("NONMEMBER")
            .memberType(MemberType.NONMEMBER)
            .room(room)
            .build();

        sessionService.setMember(member);

        RoomResponse roomResponse = new RoomResponse();
        //not set session create at property
        roomResponse.setSessionId(sessionId);
        roomResponse.setToken(sessionTokenResponse.getToken());
        roomResponse.setWss(config.remoteServiceProperties.getWss() + WS_PATH);
        roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
        roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());
        roomResponse.setSessionType(room.getSessionProperty().getSessionType());

        return new ApiResponse<>(roomResponse);
    }
}
