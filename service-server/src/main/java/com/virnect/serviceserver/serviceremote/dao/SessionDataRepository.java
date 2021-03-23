package com.virnect.serviceserver.serviceremote.dao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.domain.DeviceType;
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
import com.virnect.data.dto.rest.PushResponse;
import com.virnect.data.dto.rest.StopRecordingResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.mediaserver.core.EndReason;
import com.virnect.mediaserver.core.Participant;
import com.virnect.serviceserver.serviceremote.application.HistoryService;
import com.virnect.serviceserver.serviceremote.application.SessionTransactionalService;
import com.virnect.serviceserver.serviceremote.dto.constraint.LicenseItem;
import com.virnect.serviceserver.serviceremote.dto.constraint.PushConstants;
import com.virnect.serviceserver.serviceremote.dto.request.room.InviteRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.JoinRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.KickRoomRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.serviceserver.serviceremote.dto.request.room.RoomRequest;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.InviteRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.KickRoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomDeleteResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomDetailInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.room.RoomResponse;
import com.virnect.serviceserver.serviceremote.dto.response.rpc.ClientMetaData;
import com.virnect.serviceserver.serviceremote.dto.response.session.SessionResponse;
import com.virnect.serviceserver.serviceremote.dto.response.session.SessionTokenResponse;
import com.virnect.serviceserver.global.config.UrlConstants;
import com.virnect.serviceserver.serviceremote.application.PushMessageClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionDataRepository {

    private static final String TAG = SessionDataRepository.class.getSimpleName();

    private final ObjectMapper objectMapper;

    private final SessionTransactionalService sessionService;
    private final HistoryService historyService;

    private final PushMessageClient pushMessageClient;

    private final WorkspaceRestService workspaceRestService;
    private final UserRestService userRestService;
    private final RecordRestService recordRestService;
    private final ModelMapper modelMapper;

    public Boolean destroySession(String sessionId, EndReason reason) {

        Room room = sessionService.getRoom(sessionId);

        if (room == null) {
            LogMessage.formedError(
                TAG,
                "DESTROY SESSION EVENT",
                "destroySession",
                reason.toString(),
                ErrorCode.ERR_ROOM_NOT_FOUND.getMessage()
            );
            return false;
        } else {
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
    }

    public Boolean stopRecordSession(String sessionId) {

        Room room = sessionService.getRoom(sessionId);
        LogMessage.formedInfo(
            TAG,
            "stopRecordSession",
            "invokeDataProcess",
            "destroy session",
            sessionId
        );

        if (room != null) {
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
        } else {
            return false;
        }
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
        assert clientMetaData != null;

        log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        // Load DB Data
        Room room = sessionService.getRoom(sessionId);

        if (room == null) {
            LogMessage.formedError(
                TAG,
                "LEAVE SESSION EVENT",
                "leaveSession",
                reason.toString(),
                ErrorCode.ERR_ROOM_NOT_FOUND.getMessage()
            );
        } else {
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
        }
        return true;
    }

    public Boolean closeSession(Participant participant, String sessionId, EndReason reason) {

        ClientMetaData clientMetaData = null;
        Room room;

        // pre data process
        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert clientMetaData != null;

        log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        // Load DB Data
        room = sessionService.getRoom(sessionId);

        if (room == null) {
            LogMessage.formedInfo(
                TAG,
                "LEAVE SESSION EVENT",
                "leaveSession",
                reason.toString()
            );
            return true;
        } else {
            LogMessage.formedError(
                TAG,
                "LEAVE SESSION EVENT",
                "leaveSession",
                reason.toString(),
                ErrorCode.ERR_ROOM_CLOSE_FAIL.getMessage()
            );

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
            room.getWorkspaceId(), sessionId, clientMetaData.getClientData());
        try {
            if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                return ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID; //Code.EXISTING_USER_IN_ROOM_ERROR_CODE
            } else {
                member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                member.setConnectionId(participant.getParticipantPublicId());
                member.setMemberStatus(MemberStatus.LOAD);
            }
            sessionService.setMember(member);
                    /*if (member == null) {
                        member = setData();
                    } else {
                        if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID); //Code.EXISTING_USER_IN_ROOM_ERROR_CODE
                        } else {
                            member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                            member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                            member.setConnectionId(participant.getParticipantPublicId());
                            member.setMemberStatus(MemberStatus.LOAD);
                        }
                    }
                    sessionService.setMember(member);*/
            //sessionService.joinSession(sessionId, participant.getParticipantPublicId(), clientMetaData);
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

        Room room;
        ClientMetaData clientMetaData = null;

        // Pre data process
        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert clientMetaData != null;

        log.info("session disconnect and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session disconnect and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session disconnect and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        // Load DB Data
        room = sessionService.getRoom(sessionId);

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

        PushResponse response = null;

        UserInfoResponse userInfoResponse;

        Room room = sessionService.getRoom(sessionId);
        if (room != null) {
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

            userInfoResponse = checkUserValidation(userId);

            //send push message invite
            pushMessageClient.setPush(
                PushConstants.PUSH_SERVICE_REMOTE,
                PushConstants.SEND_PUSH_ROOM_INVITE,
                room.getWorkspaceId(),
                sessionProperty.getPublisherId(),
                targetIds
            );

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
            response = pushResponse.getData();
        }
        return response;
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert sessionResponse != null;
        assert sessionTokenResponse != null;

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
            //.restrictedMode(roomRequest.isRestrictedMode())
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

        // Process
        if (sessionService.createRoom(room) != null) {
            RoomResponse roomResponse = new RoomResponse();
            //not set session create at property
            roomResponse.setSessionId(sessionResponse.getId());
            roomResponse.setToken(sessionTokenResponse.getToken());
            roomResponse.setWss(UrlConstants.wssUrl);
            //roomResponse.setRestrictedMode(room.isRestrictedMode());
            roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
            roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());
            /*CoturnResponse coturnResponse = setCoturnResponse(room.getSessionProperty().getSessionType());
            roomResponse.getCoturn().add(coturnResponse);*/

            roomResponse.setSessionType(room.getSessionProperty().getSessionType());

            return new ApiResponse<>(roomResponse);
        } else {
            return new ApiResponse<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
        }
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert sessionResponse != null;
        assert sessionTokenResponse != null;

        RoomHistory roomHistory = historyService.getRoomHistory(roomRequest.getWorkspaceId(), preSessionId);
        if (roomHistory == null) {
            return new ApiResponse<>(ErrorCode.ERR_HISTORY_ROOM_NOT_FOUND);
        } else {
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
                //.restrictedMode(roomRequest.isRestrictedMode())
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

            if (sessionService.createRoom(room) != null) {
                RoomResponse roomResponse = new RoomResponse();
                //not set session create at property
                roomResponse.setSessionId(sessionResponse.getId());
                roomResponse.setToken(sessionTokenResponse.getToken());
                roomResponse.setWss(UrlConstants.wssUrl);
                //roomResponse.setRestrictedMode(room.isRestrictedMode());
                roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
                roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());

                //CoturnResponse coturnResponse = setCoturnResponse(room.getSessionProperty().getSessionType());
                //roomResponse.getCoturn().add(coturnResponse);

                roomResponse.setSessionType(room.getSessionProperty().getSessionType());

                return new ApiResponse<>(roomResponse);
            } else {
                return new ApiResponse<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
            }
        }
    }

    public ApiResponse<RoomInfoListResponse> loadRoomPageList(
        String workspaceId,
        String userId,
        Pageable pageable
    ) {
        //return new RepoDecoder<Page<Room>, RoomInfoListResponse>(RepoDecoderType.READ) {
        Page<Room> roomPage = sessionService.getRoomPageList(workspaceId, userId, pageable);

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

                /*Map<Room, List<Member>> roomListMap = roomPage.getContent().stream()
                        .filter(room -> {
                            if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                                return true;
                            } else {
                                for (Member member : room.getMembers()) {
                                    if (member.getUuid().equals(userId) && !member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        })
                        .collect(Collectors.toMap(room -> room, Room::getMembers));

                for (Room room : roomListMap.keySet()) {
                    log.info("mapping key invokeDataProcess: {}", room.getSessionId());
                    for (Member member : roomListMap.get(room)) {
                        log.info("mapping value invokeDataProcess: {}", member.getUuid());
                    }
                }*/

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
        return new ApiResponse<>(new RoomInfoListResponse(roomInfoList, pageMeta));
    }

    public ApiResponse<RoomInfoListResponse> searchRoomPageList(
        String workspaceId,
        String userId,
        String search,
        Pageable pageable
    ) {
        List<MemberInfoResponse> memberInfoList;

        // Prepair data process
        ApiResponse<WorkspaceMemberInfoListResponse> feignResponse = workspaceRestService.getWorkspaceMemberInfoList(
            workspaceId,
            "remote",
            search,
            pageable.getPageNumber(),
            pageable.getPageSize()
        );

        List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse.getData().getMemberInfoList();
        memberInfoList = workspaceMemberInfoList.stream()
            .map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
            .collect(Collectors.toList());

        log.info("fetchFromRepository::searchRoomPageList:: {}", feignResponse.getData().getPageMeta());

        for (MemberInfoResponse memberInfoResponse : memberInfoList) {
            log.info("fetchFromRepository::searchRoomPageList:: {}", memberInfoResponse.toString());
        }

        //search all activated room
        Page<Room> roomPage;

        // Load data
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
            roomPage = sessionService.getRoomPageList(workspaceId, userId, search, pageable);
        } else {
            log.info("loadFromDatabase::searchRoomPageList::memberInfoList is not empty");
            roomPage = sessionService.getRoomPageList(workspaceId, userId, userIds, search, pageable);
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
            List<MemberInfoResponse> memberInfoResponses = room.getMembers().stream()
                .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                .collect(Collectors.toList());

            // find and get extra information from workspace-server using uuid
            for (MemberInfoResponse memberInfoResponse : memberInfoResponses) {
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
            roomInfoResponse.setMemberList(memberInfoResponses);

            roomInfoList.add(roomInfoResponse);
        }
        return new ApiResponse<>(new RoomInfoListResponse(roomInfoList, pageMeta));
    }

    public ApiResponse<RoomInfoListResponse> loadRoomList(
        String workspaceId,
        String userId,
        Pageable pageable
    ) {
        Page<Room> roomPage = sessionService.getRoomPageList(workspaceId, userId, pageable);

        // Page Metadata Empty
        PageMetadataResponse pageMeta = PageMetadataResponse.builder()
            .currentPage(0)
            .currentSize(0)
            .numberOfElements(roomPage.getNumberOfElements())
            .totalPage(roomPage.getTotalPages())
            .totalElements(roomPage.getTotalElements())
            .last(roomPage.isLast())
            .build();

                /*Map<Room, List<Member>> roomListMap = roomPage.getContent().stream()
                        .filter(room -> {
                            if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                                return true;
                            } else {
                                for (Member member : room.getMembers()) {
                                    if (member.getUuid().equals(userId)
                                            && !member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        })
                        .collect(Collectors.toMap(room -> room, Room::getMembers));

                */
        List<RoomInfoResponse> roomInfoList = new ArrayList<>();
        //for (Room room : roomListMap.keySet()) {
        for (Room room : roomPage.getContent()) {
            RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
            roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

            // Mapping Member List Data to Member Information List
            List<MemberInfoResponse> memberInfoList = room.getMembers()
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
            roomInfoResponse.setMemberList(memberInfoList);

            roomInfoList.add(roomInfoResponse);
        }
        return new ApiResponse<>(new RoomInfoListResponse(roomInfoList, pageMeta));
    }

    public ApiResponse<RoomDetailInfoResponse> loadRoom(String workspaceId, String sessionId) {
        //return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.READ) {
        LogMessage.formedInfo(
            TAG,
            "invokeDataProcess",
            "loadRoom",
            "room info retrieve by session id",
            sessionId
        );

        List<MemberInfoResponse> memberInfoList;

        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (room == null) {
            return new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        } else {
            if (room.getRoomStatus() != RoomStatus.ACTIVE) {
                return new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
            } else {
                // mapping data
                RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(
                    room, RoomDetailInfoResponse.class);
                roomDetailInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

                // Get Member List by Room Session ID
                // Mapping Member List Data to Member Information List
                memberInfoList = sessionService.getMemberList(roomDetailInfoResponse.getSessionId())
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
                return new ApiResponse<>(roomDetailInfoResponse);
            }
        }
    }

    public ApiResponse<RoomDetailInfoResponse> updateRoom(
        String workspaceId,
        String sessionId,
        ModifyRoomInfoRequest modifyRoomInfoRequest
    ) {

        Room room;
        List<MemberInfoResponse> memberInfoList;

        //return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.UPDATE) {
        LogMessage.formedInfo(
            TAG,
            "invokeDataProcess",
            "updateRoom",
            "room info retrieve by session id",
            sessionId
        );

        room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (!ObjectUtils.isEmpty(room)) {
            String userId = room.getLeaderId();
            if (userId.equals(modifyRoomInfoRequest.getUuid())) {

                // update data
                room.setTitle(modifyRoomInfoRequest.getTitle());
                room.setDescription(modifyRoomInfoRequest.getDescription());

                Room updatedRoom = sessionService.updateRoom(room);

                // mapping data
                RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(
                    updatedRoom, RoomDetailInfoResponse.class);
                roomDetailInfoResponse.setSessionType(updatedRoom.getSessionProperty().getSessionType());
                // Get Member List by Room Session ID
                // Mapping Member List Data to Member Information List
                memberInfoList = sessionService.getMemberList(roomDetailInfoResponse.getSessionId())
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
                return new ApiResponse<>(roomDetailInfoResponse);
            } else {
                return new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
            }
        } else {
            return new ApiResponse<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        }
    }

    public ApiResponse<RoomDeleteResponse> removeRoom(String workspaceId, String sessionId, String userId) {
        //return new RepoDecoder<Room, RoomDeleteResponse>(RepoDecoderType.DELETE) {
        Room room;
        room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
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

        // Set History
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

                /*DataProcess<RoomDeleteResponse> dataProcess = null;
                try {
                    dataProcess = new DataProcess<>(RoomDeleteResponse.class);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }*/
        //log.info("ROOM INFO DELETE BY dataProcess => [{}]", dataProcess.data.toString());

        return new ApiResponse<>(new RoomDeleteResponse(sessionId, true, LocalDateTime.now()));
    }

    /**
     * Prepare to join the room the user is....
     */
    public ApiResponse<Boolean> prepareJoinRoom(String workspaceId, String sessionId, String userId) {
            
        Room room = sessionService.getRoomForWrite(workspaceId, sessionId);
        if (room == null) {
            return new ApiResponse<>(false, ErrorCode.ERR_ROOM_NOT_FOUND);
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
                    if (memberStatus.equals(MemberStatus.UNLOAD)) {
                        member.setMemberStatus(MemberStatus.LOADING);
                        sessionService.setMember(member);
                        result = true;
                    } else if (memberStatus.equals(MemberStatus.EVICTED)) {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_EVICTED_STATUS;
                    } else if (memberStatus.equals(MemberStatus.LOAD)) {
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
                    if (memberStatus.equals(MemberStatus.UNLOAD)) {
                        member.setMemberStatus(MemberStatus.LOADING);
                        sessionService.setMember(member);
                        result = true;
                    } else if (memberStatus.equals(MemberStatus.EVICTED)) {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_EVICTED_STATUS;
                    } else if (memberStatus.equals(MemberStatus.LOAD)) {
                        errorCode = ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED;
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
        String workspaceId, String sessionId, String sessionToken, JoinRoomRequest joinRoomRequest
    ) {
        ApiResponse<RoomResponse> joinRoomResponse;

        SessionTokenResponse sessionTokenResponse = null;

        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);

        if (room == null) {
            joinRoomResponse = new ApiResponse<>(new RoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        } else {
            // Pre data process
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

            if (errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                RoomResponse roomResponse = new RoomResponse();
                //not set session create at property
                roomResponse.setSessionId(sessionId);
                roomResponse.setToken(sessionTokenResponse.getToken());
                roomResponse.setWss(UrlConstants.wssUrl);
                roomResponse.setVideoRestrictedMode(room.isVideoRestrictedMode());
                roomResponse.setAudioRestrictedMode(room.isAudioRestrictedMode());
                roomResponse.setSessionType(room.getSessionProperty().getSessionType());

                joinRoomResponse = new ApiResponse<>(roomResponse);
            } else {
                joinRoomResponse = new ApiResponse<>(new RoomResponse(), errorCode);
            }
        }
        return joinRoomResponse;
    }

    public ApiResponse<ResultResponse> exitRoom(String workspaceId, String sessionId, String userId) {
        Member member = null;

        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (room == null) {
            return new ApiResponse<>(new ResultResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        } else {
            for (Member participant : room.getMembers()) {
                if (participant.getUuid().equals(userId)) {
                    member = participant;
                }
            }
            if (member == null) {
                return new ApiResponse<>(new ResultResponse(), ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
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
                    sessionService.removeMember(room, member);
                    ResultResponse resultResponse = new ResultResponse();
                    resultResponse.setUserId(userId);
                    resultResponse.setResult(true);
                    return new ApiResponse<>(resultResponse);
                } else {
                    return new ApiResponse<>(new ResultResponse(), errorCode);
                }
            }
        }
    }

    public ApiResponse<KickRoomResponse> kickFromRoom(
        String workspaceId, String sessionId, KickRoomRequest kickRoomRequest
    ) {

        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (room == null) {
            return new ApiResponse<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
        }

        Member member = null;
        for (Member participant : room.getMembers()) {
            if (participant.getUuid().equals(kickRoomRequest.getParticipantId())) {
                member = participant;
            }
        }

        if (member == null) {
            return new ApiResponse<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
        } else {
            if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
                return new ApiResponse<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
            } else {
                String connectionId = member.getConnectionId();

                KickRoomResponse kickRoomResponse = new KickRoomResponse();
                kickRoomResponse.setWorkspaceId(room.getWorkspaceId());
                kickRoomResponse.setSessionId(room.getSessionId());
                kickRoomResponse.setLeaderId(room.getLeaderId());
                kickRoomResponse.setParticipantId(kickRoomRequest.getParticipantId());
                kickRoomResponse.setConnectionId(connectionId);
                //if connection id cannot find, push message and just remove user
                if (connectionId == null || connectionId.isEmpty()) {
                    sessionService.updateMember(member, MemberStatus.EVICTED);
                }
                return new ApiResponse<>(kickRoomResponse);
            }
        }
    }

    public ApiResponse<InviteRoomResponse> inviteMember(
        String workspaceId, String sessionId, InviteRoomRequest inviteRoomRequest
    ) {
        //return new RepoDecoder<Room, InviteRoomResponse>(RepoDecoderType.UPDATE) {
        Room room = sessionService.getRoom(workspaceId, sessionId).orElse(null);
        if (room == null)
            return new ApiResponse<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);

        if (!room.getLeaderId().equals(inviteRoomRequest.getLeaderId())) {
            return new ApiResponse<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        } else {
            List<Member> members = room.getMembers().stream()
                .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
                .collect(Collectors.toList());

            //remove if member status is evicted
            //room.getMembers().removeIf(member -> member.getRoom() == null);
            //room.getMembers().removeIf(member -> member.getMemberStatus().equals(MemberStatus.EVICTED));

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
}
