package com.virnect.serviceserver.gateway.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.core.EndReason;
import com.virnect.serviceserver.core.Participant;
import com.virnect.serviceserver.core.Session;
import com.virnect.serviceserver.gateway.dao.MemberHistoryRepository;
import com.virnect.serviceserver.gateway.dao.MemberRepository;
import com.virnect.serviceserver.gateway.dao.RoomHistoryRepository;
import com.virnect.serviceserver.gateway.dao.RoomRepository;
import com.virnect.serviceserver.gateway.domain.*;
import com.virnect.serviceserver.gateway.dto.CoturnResponse;
import com.virnect.serviceserver.gateway.dto.PageMetadataResponse;
import com.virnect.serviceserver.gateway.dto.SessionResponse;
import com.virnect.serviceserver.gateway.dto.SessionTokenResponse;
import com.virnect.serviceserver.gateway.dto.request.*;
import com.virnect.serviceserver.gateway.dto.response.*;
import com.virnect.serviceserver.gateway.dto.rest.*;
import com.virnect.serviceserver.gateway.dto.rpc.ClientMetaData;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import com.virnect.serviceserver.gateway.global.constants.LicenseConstants;
import com.virnect.serviceserver.gateway.global.constants.PushConstrants;
import com.virnect.serviceserver.gateway.global.constants.ServiceConstants;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import com.virnect.serviceserver.gateway.infra.file.Default;
import com.virnect.serviceserver.gateway.service.*;
import com.virnect.serviceserver.kurento.core.KurentoSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * "uuid": "45d974ac8cbd3f66b26b0179604d6742",
 * "email": "test11@test.com",
 * "name": "홍길동11",
 * "firstName": "길동11",
 * "lastName": "홍",
 * "nickname": "왕밤빵11",
 * "description": "테스트 유저 입니다.",
 * "profile": "default",
 * "birth": "2020-04-16",
 * "mobile": "010-1234-1234",
 * "recoveryEmail": "test11@test.com",
 * "loginLock": "INACTIVE",
 * "userType": "USER",
 * "marketInfoReceive": "ACCEPT",
 * "createdDate": "2020-04-17T03:54:10",
 * "updatedDate": "2020-04-17T03:54:10"
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class RemoteGatewayService {
    private static final String TAG = "MemberRestController";

    //private final RemoteServiceRestService remoteServiceRestService;
    private final RemoteServiceConfig config;
    // feign client
    private final MessageRestService messageRestService;
    private final LicenseRestService licenseRestService;
    private final UserRestService userRestService;
    private final WorkspaceRestService workspaceRestService;
    private final ModelMapper modelMapper;
    //private final FileUploadService fileUploadService;

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;

    //
    private final RoomHistoryRepository roomHistoryRepository;
    private final MemberHistoryRepository memberHistoryRepository;

    public ApiResponse<LicenseInfoListResponse> getLicenseValidity(String workspaceId, String userId) {
       /* ApiResponse<LicenseInfoListResponse> licenseInfoList = licenseRestService.getUserLicenseValidation(workspaceId, userId);
        LicenseInfoResponse licenseInfo = null;
        for (LicenseInfoResponse licenseInfoResponse : licenseInfoList.getData().getLicenseInfoList()) {
            if(licenseInfoResponse.getLicenseType().equals(LicenseConstants.PRODUCT_NAME)) {
                licenseInfo = licenseInfoResponse;
            }
        }
        return licenseInfo;*/
        return licenseRestService.getUserLicenseValidation(workspaceId, userId);
    }

    public ApiResponse<PushResponse> sendPushMessage(PushSendRequest pushSendRequest) {
        return messageRestService.sendPush(pushSendRequest);
    }

    public MemberType getUserGrantValidity(String sessionId, String userId) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        if(room.getMembers().isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
        } else {
            for (Member member : room.getMembers()) {
                if(member.getUuid().equals(userId)) {
                    return member.getMemberType();
                }
            }
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
        }
    }

    /*public ApiResponse<SessionListResponse> getSessionList(boolean webRtcStats) {
        //ApiResponse<SessionListResponse> response = this.remoteServiceRestService.getServiceSessions(webRtcStats);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SessionListResponse response = null;
        try {
            response = objectMapper.readValue(this.remoteServiceRestService.getServiceSessions(webRtcStats), SessionListResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert response != null;
        if(response.getNumberOfElements() != 0) {
            log.debug("getSessionList: " + response.getNumberOfElements());
            log.debug("getSessionList: " + response.getContent().get(0).toString());
        } else {
            log.debug("getSessionList: has no any data");
        }
        return new ApiResponse<>(response);
    }*/

    //===========================================  Template Session Services     =================================================//








   /* @Transactional
    public void createSession(String sessionId, Long startTime) {
        log.debug("tempCreateRoom");
        // Remote Room Entity Create
        Room room = Room.builder()
                .sessionId(sessionId)
                .title(sessionId)
                .description("Test Session des")
                .leaderId("410df50ca6e32db0b6acba09bcb457ff")
                .workspaceId("40f9bbee9d85dca7a34a0dd205aae718")
                .build();

        // Remote Session Property Entity Create
        SessionProperty sessionProperty = SessionProperty.builder()
                .mediaMode("ROUTED")
                .recordingMode("MANUAL")
                .defaultOutputMode("COMPOSED")
                .defaultRecordingLayout("BEST_FIT")
                .recording(false)
                .room(room)
                .build();

        room.setSessionProperty(sessionProperty);
        room.setProfile(Default.ROOM_PROFILE.getValue());

        //
        Member member = Member.builder()
                .room(room)
                .memberType(MemberType.LEADER)
                .uuid("410df50ca6e32db0b6acba09bcb457ff")
                .email("test18@test.com")
                .sessionId(room.getSessionId())
                .build();

        member.setMemberStatus(MemberStatus.LOAD);
        room.getMembers().add(member);

        roomRepository.save(room);
    }*/

    //================================================================================================================//


    //===========================================  Room Services     =================================================//
    @Transactional
    public ApiResponse<RoomResponse> createRoom(RoomRequest roomRequest,
                                                RoomProfileUpdateRequest roomProfileUpdateRequest,
                                                String session,
                                                String sessionToken) {
        log.debug("createRoom: " + roomRequest.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        //Map<?, ?> map = objectMapper.convertValue(request, Map.class);
        SessionResponse sessionResponse = null;

        try {
            sessionResponse = objectMapper.readValue(session, SessionResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        SessionTokenResponse sessionTokenResponse = null;
        try {
            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert sessionResponse != null;
        assert sessionTokenResponse != null;

        // Remote Room Entity Create
        Room room = Room.builder()
                .sessionId(sessionResponse.getId())
                .title(roomRequest.getTitle())
                .description(roomRequest.getDescription())
                .leaderId(roomRequest.getLeaderId())
                .workspaceId(roomRequest.getWorkspaceId())
                .build();

        // Remote Session Property Entity Create
        SessionProperty sessionProperty = SessionProperty.builder()
                .mediaMode("ROUTED")
                .recordingMode("MANUAL")
                .defaultOutputMode("COMPOSED")
                .defaultRecordingLayout("BEST_FIT")
                .recording(false)
                .room(room)
                .build();

        room.setSessionProperty(sessionProperty);

        // profile image request
        // not ready to upload
        room.setProfile(Default.ROOM_PROFILE.getValue());
        /*if(roomProfileUpdateRequest.getProfile() != null) {
            try {
                String profileUrl = this.fileUploadService.upload(roomProfileUpdateRequest.getProfile());
                remoteRoom.setProfile(profileUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            remoteRoom.setProfile(Default.ROOM_PROFILE.getValue());
        }*/
        // set room members
        if(!roomRequest.getLeaderId().isEmpty() && !roomRequest.getLeaderEmail().isEmpty()) {
            log.debug("leader Id is {}", roomRequest.getLeaderId());
            Member member = Member.builder()
                    .room(room)
                    .memberType(MemberType.LEADER)
                    .workspaceId(roomRequest.getWorkspaceId())
                    .uuid(roomRequest.getLeaderId())
                    .email(roomRequest.getLeaderEmail())
                    .sessionId(room.getSessionId())
                    .build();

            room.getMembers().add(member);
        } else {
            log.debug("leader Id is null");
        }

        if(!roomRequest.getParticipants().isEmpty()) {
            for (RoomRequest.Participant participant : roomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .memberType(MemberType.UNKNOWN)
                        .workspaceId(roomRequest.getWorkspaceId())
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();

                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }

        RoomResponse roomResponse = new RoomResponse();
        roomRepository.save(room);

        //not set session create at property
        roomResponse.setSessionId(sessionResponse.getId());
        //roomResponse.setRoomId(sessionTokenResponse.getId());
        roomResponse.setToken(sessionTokenResponse.getToken());
        roomResponse.setWss(ServiceServerApplication.wssUrl);
        for (String coturnUrl: config.getCoturnUrisList()) {
            CoturnResponse coturnResponse = new CoturnResponse();
            coturnResponse.setUsername(config.getCoturnUsername());
            coturnResponse.setCredential(config.getCoturnCredential());
            coturnResponse.setUrl(coturnUrl);
            roomResponse.getCoturn().add(coturnResponse);
        }
        return new ApiResponse<>(roomResponse);
    }

    @Transactional
    public void createSession(Session sessionNotActive) {
        log.info("session create and sessionEventHandler is here");
        Room room = roomRepository.findBySessionId(sessionNotActive.getSessionId()).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        //set active time
        LocalDateTime activeTime = LocalDateTime.now();
        room.setActiveDate(activeTime);
        room.setRoomStatus(RoomStatus.ACTIVE);
        roomRepository.save(room);
    }

    public ApiResponse<RoomInfoListResponse> getRoomInfoList(String workspaceId, String userId, boolean paging, Pageable pageable) {
        log.debug("getRoomInfoList");
        if(!paging) {
            List<Room> roomList = new ArrayList<>();
            List<Member> memberList;
            memberList = this.memberRepository.findByWorkspaceIdAndUuidAndRoomNotNull(workspaceId, userId);
            for(Member member : memberList ) {
                if(member.getRoom().getRoomStatus().equals(RoomStatus.ACTIVE)) {
                    roomList.add(member.getRoom());
                }
            }

            //roomList = this.roomRepository.findByWorkspaceId(workspaceId);
            List<RoomInfoResponse> roomInfoList = roomList.stream()
                    .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                    .collect(Collectors.toList());

            // Page Metadata Empty
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(0)
                    .currentSize(0)
                    .totalPage(0)
                    .totalElements(0)
                    .build();

            // Get Member List by Room Session Ids
            for (RoomInfoResponse response: roomInfoList) {
                List<Member> roomMemberList = this.memberRepository.findByWorkspaceIdAndSessionIdAndRoomNotNull(workspaceId, response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = roomMemberList.stream()
                        .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if(!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }
                }
                // Set Member List to Room Information Response
                response.setMemberList(memberInfoList);
                //log.debug("getRoomInfoList: {}", response.toString());
            }
            return new ApiResponse<>(new RoomInfoListResponse(roomInfoList, pageMeta));
        } else {
            //Page<Room> roomPage;
            List<Room> roomList = new ArrayList<>();
            Page<Member> memberPage;
            memberPage = this.memberRepository.findByWorkspaceIdAndUuidAndRoomNotNull(workspaceId, userId, pageable);

            for(Member member : memberPage.getContent() ) {
                if(member.getRoom().getRoomStatus().equals(RoomStatus.ACTIVE)) {
                    roomList.add(member.getRoom());
                }
            }
            //roomPage = this.roomRepository.findAll(pageable);
            List<RoomInfoResponse> roomInfoList = roomList.stream()
                    .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                    .collect(Collectors.toList());

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(pageable.getPageNumber())
                    .currentSize(pageable.getPageSize())
                    .totalPage(memberPage.getTotalPages())
                    .totalElements(memberPage.getNumberOfElements())
                    .build();

            //roomInfoList.forEach(info -> log.info("{}", info));
            log.info("Paging Metadata: {}", pageMeta.toString());

            // Get Member List by Room Session Ids
            for (RoomInfoResponse response: roomInfoList) {
                List<Member> roomMemberList = this.memberRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = roomMemberList.stream()
                        .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if(!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }
                }
                // Set Member List to Room Information Response
                response.setMemberList(memberInfoList);
            }
            return new ApiResponse<>(new RoomInfoListResponse(roomInfoList, pageMeta));
        }
    }

    @Transactional
    public ApiResponse<Boolean> removeAllRoom(String workspaceId) {
        log.info("ROOM INFO DELETE BY SESSION ID => [{}]", workspaceId);
        // Get Specific Room using Session ID
        List<Room> roomList = roomRepository.findByWorkspaceId(workspaceId);

        for (Room room: roomList) {
            RoomHistory roomHistory = RoomHistory.builder()
                    .sessionId(room.getSessionId())
                    .title(room.getTitle())
                    .description(room.getDescription())
                    .profile(room.getProfile())
                    .leaderId(room.getLeaderId())
                    .workspaceId(room.getWorkspaceId())
                    .build();

            // Set room member history
            // Get Member List by Room Session Ids
            List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
            // Mapping Member List Data to Member History List
            for (Member member : memberList) {
                MemberHistory memberHistory = MemberHistory.builder()
                        .roomHistory(roomHistory)
                        .workspaceId(member.getWorkspaceId())
                        .uuid(member.getUuid())
                        .email(member.getEmail())
                        .memberType(member.getMemberType())
                        .deviceType(member.getDeviceType())
                        .sessionId(member.getSessionId())
                        .startDate(member.getStartDate())
                        .endDate(member.getEndDate())
                        .durationSec(member.getDurationSec())
                        .build();
                memberHistoryRepository.save(memberHistory);
                roomHistory.getMemberHistories().add(memberHistory);
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
            roomHistoryRepository.save(roomHistory);

            //delete room
            roomRepository.delete(room);
        }

        return new ApiResponse<>(true);
    }

    @Transactional
    public ApiResponse<ResultResponse> removeRoom(String workspaceId, String sessionId, String userId) {
        log.info("ROOM INFO DELETE BY SESSION ID => [{}]", sessionId);
        // Get Specific Room using Session ID
        /*Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));*/
        Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
        Member member = memberRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, userId).orElse(null);;

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        ApiResponse<ResultResponse> response = new ApiResponse<>(resultResponse);

        if(room == null) {
            response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
            response.setData(response.getData());
            return response;
        } else if(member == null) {
            response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
            response.setData(response.getData());
            return response;
        } else {
            if(member.getMemberStatus().equals(MemberStatus.LOAD)) {
                response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED);
                response.setData(response.getData());
                return response;
            }

            // Room and Member History 저장
            // Remote Room History Entity Create
            RoomHistory roomHistory = RoomHistory.builder()
                    .sessionId(room.getSessionId())
                    .title(room.getTitle())
                    .description(room.getDescription())
                    .profile(room.getProfile())
                    .leaderId(room.getLeaderId())
                    .workspaceId(room.getWorkspaceId())
                    .build();

            // Set room member history
            // Get Member List by Room Session Ids
            List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
            // Mapping Member List Data to Member History List
            for (Member roomMember : memberList) {
                MemberHistory memberHistory = MemberHistory.builder()
                        .roomHistory(roomHistory)
                        .workspaceId(roomMember.getWorkspaceId())
                        .uuid(roomMember.getUuid())
                        .email(roomMember.getEmail())
                        .memberType(roomMember.getMemberType())
                        .deviceType(roomMember.getDeviceType())
                        .sessionId(roomMember.getSessionId())
                        .startDate(roomMember.getStartDate())
                        .endDate(roomMember.getEndDate())
                        .durationSec(roomMember.getDurationSec())
                        .build();
                memberHistoryRepository.save(memberHistory);
                roomHistory.getMemberHistories().add(memberHistory);
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
            roomHistoryRepository.save(roomHistory);

            //delete room
            roomRepository.delete(room);

            resultResponse.setResult(true);
            return new ApiResponse<>(resultResponse);
        }
    }

    @Transactional
    public void destroySession(KurentoSession session, EndReason reason) {
        log.info("session destroy and sessionEventHandler is here: {}", session.getSessionId());
        //Room room = roomRepository.findBySessionId(session.getSessionId()).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        Room room = roomRepository.findBySessionId(session.getSessionId()).orElse(null);
        if(room == null) {
            log.info("session destroy and sessionEventHandler is faild. room data is null");
        } else {
            // Room and Member History 저장
            // Remote Room History Entity Create
            RoomHistory roomHistory = RoomHistory.builder()
                    .sessionId(room.getSessionId())
                    .title(room.getTitle())
                    .description(room.getDescription())
                    .profile(room.getProfile())
                    .leaderId(room.getLeaderId())
                    .workspaceId(room.getWorkspaceId())
                    .build();

            // Set room member history
            // Get Member List by Room Session Ids
            List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
            // Mapping Member List Data to Member History List
            for (Member member : memberList) {
                MemberHistory memberHistory = MemberHistory.builder()
                        .roomHistory(roomHistory)
                        .workspaceId(member.getWorkspaceId())
                        .uuid(member.getUuid())
                        .email(member.getEmail())
                        .memberType(member.getMemberType())
                        .deviceType(member.getDeviceType())
                        .sessionId(member.getSessionId())
                        .startDate(member.getStartDate())
                        .endDate(member.getEndDate())
                        .durationSec(member.getDurationSec())
                        .build();
                memberHistoryRepository.save(memberHistory);
                roomHistory.getMemberHistories().add(memberHistory);
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
            roomHistoryRepository.save(roomHistory);

            //delete room
            roomRepository.delete(room);
        }
    }

    public ApiResponse<RoomDetailInfoResponse> getRoomInfoBySessionId(String workspaceId, String sessionId) {
        log.info("ROOM INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
        // Get Specific Room using Session ID
        /*Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));*/

        Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
        /*Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).empty().orElse(
                Room.builder().build()
        );*/

        ApiResponse<RoomDetailInfoResponse> response = new ApiResponse<>();
        if(room == null) {
            response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
            return response;
        } else {
            if (room.getRoomStatus() != RoomStatus.ACTIVE) {
                response.setErrorResponseData(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
                return response;
            } else {
                RoomDetailInfoResponse resultResponse;
                // mapping data
                //RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);

                // Get Member List by Room Session ID
                List<Member> memberList = this.memberRepository.findAllBySessionId(resultResponse.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberList.stream()
                        .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }
                }
                // Set Member List to Room Detail Information Response
                resultResponse.setMemberList(memberInfoList);
                return new ApiResponse<>(resultResponse);
            }
        }
        /*if(room.getRoomStatus() != RoomStatus.ACTIVE) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
        }*/
    }

    @Transactional
    public ApiResponse<RoomResponse>joinRoom(String workspaceId, String sessionId, String sessionToken, JoinRoomRequest joinRoomRequest) {
        /*Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));*/

        Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
        ApiResponse<RoomResponse> response = new ApiResponse<>();
        if(room == null) {
            response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
            return response;
        }


        for (Member member: room.getMembers()) {
            if(member.getUuid().equals(joinRoomRequest.getUuid())) {
                log.debug("Room has member Id is {}", member.getUuid());

                if(member.getMemberStatus().equals(MemberStatus.LOAD)) {
                    response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                    return response;
                }

                SessionTokenResponse sessionTokenResponse = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                assert sessionTokenResponse != null;

                // set room members status
                /*Member member = Member.builder()
                        .room(room)
                        .memberType(joinRoomRequest.getMemberType())
                        .workspaceId(workspaceId)
                        .uuid(joinRoomRequest.getUuid())
                        .email(joinRoomRequest.getEmail())
                        .sessionId(room.getSessionId())
                        .build();*/

                member.setMemberType(joinRoomRequest.getMemberType());
                member.setDeviceType(joinRoomRequest.getDeviceType());
                member.setMemberStatus(MemberStatus.LOAD);
                //save member
                memberRepository.save(member);
                //room.getMembers().add(member);
                //roomRepository.save(room);

                RoomResponse roomResponse = new RoomResponse();
                //not set session create at property
                roomResponse.setSessionId(sessionId);
                roomResponse.setToken(sessionTokenResponse.getToken());

                roomResponse.setWss(ServiceServerApplication.wssUrl);
                for (String coturnUrl: config.getCoturnUrisList()) {
                    CoturnResponse coturnResponse = new CoturnResponse();
                    coturnResponse.setUsername(config.getCoturnUsername());
                    coturnResponse.setCredential(config.getCoturnCredential());
                    coturnResponse.setUrl(coturnUrl);
                    roomResponse.getCoturn().add(coturnResponse);
                }

                return new ApiResponse<>(roomResponse);
            } /*else {
                throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_ALLOWED);
            }*/
        }
        throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);

        //if(room.getMembers().size() >= ServiceConstants.PRODUCT_BASIC_MAX_USER) {
        //    throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
        //} else {

        //}
    }

    @Transactional
    public void joinSession(Participant participant, String sessionId, Set<Participant> existingParticipants, Integer transactionId) {
        log.info("session join and sessionEventHandler is here:[participant] {}", participant);
        log.info("session join and sessionEventHandler is here:[sessionId] {}", sessionId);
        log.info("session join and sessionEventHandler is here:[transactionId] {}", transactionId);
        log.info("session join and sessionEventHandler is here:[existingParticipants] {}", existingParticipants);
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ClientMetaData clientMetaData = null;
        try {
            clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert clientMetaData != null;

        log.info("session join and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
        log.info("session join and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
        log.info("session join and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

        for (Member member:room.getMembers()) {
            if(member.getUuid().equals(clientMetaData.getClientData())) {
                member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                member.setMemberStatus(MemberStatus.LOAD);
            }
           /* if(member.getUuid().equals(participant.getParticipantPublicId())) {
                member.setMemberStatus(MemberStatus.LOAD);
            }*/
        }
        //Member member;
        /*if(existingParticipants.isEmpty()) {
            // set room members
            member = room.getMembers().forEach(m);
            member = Member.builder()
                    .room(room)
                    .memberType(MemberType.LEADER)
                    .uuid(participant.getParticipantPublicId())
                    .email("")
                    .sessionId(room.getSessionId())
                    .build();
            member.setMemberStatus(MemberStatus.LOAD);
        } else {
            // set room members
            member = Member.builder()
                    .room(room)
                    .memberType(MemberType.WORKER)
                    .uuid(participant.getParticipantPublicId())
                    .email("")
                    .sessionId(room.getSessionId())
                    .build();
            member.setMemberStatus(MemberStatus.LOAD);

        }
        room.getMembers().add(member);*/

        roomRepository.save(room);
    }

    /**
     * This method is not for leader user
     * @param sessionId
     * @param userId
     * @return
     */
    @Transactional
    public ApiResponse<ResultResponse> exitRoom(String workspaceId, String sessionId, String userId) {
        Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
        Member member = memberRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, userId).orElse(null);;

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        ApiResponse<ResultResponse> response = new ApiResponse<>(resultResponse);
        if(room == null) {
            response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
            response.setData(response.getData());
            return response;
        } else if(member == null) {
            response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
            response.setData(response.getData());
            return response;
        } else {
            if(room.getMembers().isEmpty()) {
                response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                response.setData(response.getData());
                return response;
            } else {
                for (Member roomMember : room.getMembers()) {
                    if(member.getUuid().equals(roomMember.getUuid())) {
                        if(member.getMemberType().equals(MemberType.LEADER)) {
                            response.setErrorResponseData(ErrorCode.ERR_ROOM_LEADER_INVALID_EXIT);
                            response.setData(response.getData());
                            return response;
                        } else if(member.getMemberStatus().equals(MemberStatus.LOAD)) {
                            response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED);
                            response.setData(response.getData());
                            return response;
                        } else {
                            room.getMembers().remove(roomMember);
                            roomRepository.save(room);

                            resultResponse.setResult(true);
                            return new ApiResponse<>(resultResponse);
                        }
                    }
                    /*if(roomMember.getUuid().equals(userId)) {
                        //this.memberRepository.delete(member);
                        room.getMembers().remove(roomMember);
                        roomRepository.save(room);

                        resultResponse.setResult(true);
                        return new ApiResponse<>(resultResponse);
                    }*/
                }
                response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                response.setData(response.getData());
                return response;
            }
        }
    }

    @Transactional
    public void leaveSession(Participant participant, String sessionId, Set<Participant> remainingParticipants, Integer transactionId, EndReason reason) {
        log.info("session leave and sessionEventHandler is here:[participant] {}", participant);
        log.info("session leave and sessionEventHandler is here:[reason] {}", participant.getClientMetadata());
        log.info("session leave and sessionEventHandler is here:[sessionId] {}", sessionId);
        log.info("session leave and sessionEventHandler is here:[remainingParticipants] {}", remainingParticipants);
        log.info("session leave and sessionEventHandler is here:[transactionId] {}", transactionId);
        log.info("session leave and sessionEventHandler is here:[reason] {}", reason);

        //Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        Room room = roomRepository.findBySessionId(sessionId).orElse(null);
        if(room == null) {
            log.info("session leave and sessionEventHandler is faild. room data is null");
        } else {

            JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClientMetaData clientMetaData = null;
            try {
                clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            assert clientMetaData != null;

            log.info("session join and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
            log.info("session join and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
            log.info("session join and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

            if (room.getMembers().isEmpty()) {
                log.info("session leave and sessionEventHandler is here: room members empty");
            } else {
                for (Member member : room.getMembers()) {
                    if (member.getUuid().equals(clientMetaData.getClientData())) {
                        member.setMemberStatus(MemberStatus.UNLOAD);
                        //set end time
                        LocalDateTime endTime = LocalDateTime.now();
                        member.setEndDate(endTime);

                        //time diff seconds
                        Long totalDuration = member.getDurationSec();
                        Duration duration = Duration.between(member.getStartDate(), endTime);
                        member.setDurationSec(totalDuration + duration.getSeconds());

                        //save member
                        memberRepository.save(member);
                    }
                }
                //log.info("session leave and sessionEventHandler is here: room members not found");
            }
        }
    }

    @Transactional
    public ApiResponse<RoomDetailInfoResponse> modifyRoomInfo(String workspaceId, String sessionId, ModifyRoomInfoRequest modifyRoomInfoRequest, RoomProfileUpdateRequest roomProfileUpdateRequest) {
        log.info("ROOM INFO UPDATE BY SESSION ID => [{}]", sessionId);
        Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        room.setTitle(modifyRoomInfoRequest.getTitle());
        room.setDescription(modifyRoomInfoRequest.getDescription());

        // new profile image
        room.setProfile(Default.ROOM_PROFILE.getValue());
        /*if(roomProfileUpdateRequest.getProfile() != null) {
            try {
                String profileUrl = this.fileUploadService.upload(roomProfileUpdateRequest.getProfile());
                this.fileUploadService.delete(room.getProfile());
                room.setProfile(profileUrl);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            room.setProfile(Default.ROOM_PROFILE.getValue());
        }*/
        this.roomRepository.save(room);

        // mapping data
        RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);

        // Get Member List by Room Session ID
        List<Member> memberList = this.memberRepository.findAllBySessionId(resultResponse.getSessionId());

        // Mapping Member List Data to Member Information List
        List<MemberInfoResponse> memberInfoList = memberList.stream()
                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                .collect(Collectors.toList());

        // find and get extra information from use-server using uuid
        if (!memberInfoList.isEmpty()) {
            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                log.debug("getUsers: " + userInfo.getData().toString());

                memberInfoResponse.setEmail(userInfo.getData().getEmail());
                memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                memberInfoResponse.setLastName(userInfo.getData().getLastName());
                memberInfoResponse.setNickname(userInfo.getData().getNickname());
                memberInfoResponse.setProfile(userInfo.getData().getProfile());
            }
        }
        // Set Member List to Room Detail Information Response
        resultResponse.setMemberList(memberInfoList);


        return new ApiResponse<>(resultResponse);
    }

    @Transactional
    public ApiResponse<Boolean> kickFromRoom(String workspaceId, String sessionId, @Valid KickRoomRequest kickRoomRequest) {
        log.info("ROOM INFO KICK USER BY LEADER ID => [{}]", kickRoomRequest.getLeaderId());
        Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(room.getMembers().isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
        } else if(!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        } else {
            for (Member member : room.getMembers()) {
                if(member.getUuid().equals(kickRoomRequest.getParticipantId())) {
                    //this.memberRepository.delete(member);
                    room.getMembers().remove(member);
                    roomRepository.save(room);
                    return new ApiResponse<>(true);
                }
            }
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
        }
    }

    /*@Transactional
    public ApiResponse<Boolean> inviteRoom(String workspaceId ,String sessionId, InviteRoomRequest inviteRoomRequest) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .workspaceId(workspaceId)
                        .memberType(MemberType.WORKER)
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();
                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }

        roomRepository.save(room);

        return new ApiResponse<>(true);
    }*/

    @Transactional
    public ApiResponse<InviteRoomResponse> inviteRoom(
            String workspaceId ,
            String sessionId,
            InviteRoomRequest inviteRoomRequest) {

        PushSendRequest pushSendRequest = new PushSendRequest();
        pushSendRequest.setWorkspaceId(workspaceId);
        pushSendRequest.setUserId(inviteRoomRequest.getLeaderId());
        pushSendRequest.setEvent(PushConstrants.SEND_PUSH_ROOM_INVITE);
        for (InviteRoomRequest.Participant participant: inviteRoomRequest.getParticipants()) {
            pushSendRequest.getTargetUserIds().add(participant.getId());
        }

        //pushSendRequest.setContents();


        ApiResponse<PushResponse> pushResponse = messageRestService.sendPush(pushSendRequest);
        log.info("getPush message {}", pushResponse.toString());

        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .workspaceId(workspaceId)
                        .memberType(MemberType.UNKNOWN)
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();
                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }
        roomRepository.save(room);

        InviteRoomResponse inviteRoomResponse = new InviteRoomResponse();
        inviteRoomResponse.setEvent(pushResponse.getData().getEvent());
        inviteRoomResponse.setWorkspaceId(pushResponse.getData().getWorkspaceId());
        inviteRoomResponse.setSessionId(sessionId);
        inviteRoomResponse.setPublished(pushResponse.getData().isPublished());
        inviteRoomResponse.setPublishDate(pushResponse.getData().getPublishDate());

        return new ApiResponse<>(inviteRoomResponse);
    }

    /*public ApiResponse<Boolean> inviteRoomAccept(String workspaceId, String sessionId, String userId, Boolean accept, Locale locale) {
        if(accept) {

        } else {

        }
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .workspaceId(workspaceId)
                        .memberType(MemberType.WORKER)
                        .uuid(participant.getId())
                        .email(participant.getEmail())
                        .sessionId(room.getSessionId())
                        .build();
                room.getMembers().add(member);
            }
        } else {
            log.debug("participants Id List is null");
        }

        roomRepository.save(room);

        return new ApiResponse<>(true);
    }*/



    //================================================================================================================//
    //========================================== History Services =====================================//
    public ApiResponse<RoomHistoryInfoListResponse> getRoomHistoryInfoList(String workspaceId, String userId, boolean paging, Pageable pageable) {
        log.debug("getRoomHistoryInfoList");
        if (!paging) {
            // get all member history by uuid
            List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(0)
                    .currentSize(0)
                    .totalPage(0)
                    .totalElements(0)
                    .build();

            // find specific member has room history
            List<RoomHistory> roomHistoryList = new ArrayList<>();
            memberHistoryList.forEach(memberHistory -> {
                if (memberHistory.getRoomHistory() != null) {
                    roomHistoryList.add(memberHistory.getRoomHistory());
                }
            });

            List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());

            // Get Member List by Room Session Ids
            for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                List<MemberHistory> memberList = this.memberHistoryRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberList.stream()
                        .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setMemberStatus(MemberStatus.UNLOAD);
                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }

                    Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                        @Override
                        public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                            if(t1.getMemberType().equals(MemberType.LEADER)) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                // Set Member List to Room Information Response
                response.setMemberList(memberInfoList);
            }
            return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));

        } else {
            //Page<RoomHistory> roomPage;

            // get all member history by uuid
            //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);

            Page<MemberHistory> memberPage;
            memberPage = this.memberHistoryRepository.findByWorkspaceIdAndUuidAndRoomHistoryIsNotNull(workspaceId, userId, pageable);
            // get all member history by uuid
            //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);

            // find specific member has room history
           /* List<RoomHistory> roomHistoryList = new ArrayList<>();
            memberHistoryList.forEach(memberHistory -> {
                if (memberHistory.getRoomHistory() != null) {
                    roomHistoryList.add(memberHistory.getRoomHistory());
                }
            });*/

            //roomPage = this.roomHistoryRepository.findAll(pageable);
            // find specific member has room history
            List<RoomHistory> roomHistoryList = new ArrayList<>();
            memberPage.getContent().forEach(memberHistory -> {
                //if (memberHistory.getRoomHistory() != null) {
                    roomHistoryList.add(memberHistory.getRoomHistory());
                //}
            });

            List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());

            /*List<RoomHistoryInfoResponse> roomHistoryInfoList = roomPage.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());*/

            /*List<RoomHistoryInfoResponse> roomHistoryInfoList = roomPage.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());*/

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(pageable.getPageNumber())
                    .currentSize(pageable.getPageSize())
                    .totalPage(memberPage.getTotalPages())
                    .totalElements(memberPage.getNumberOfElements())
                    .build();

            //roomInfoList.forEach(info -> log.info("{}", info));
            log.info("Paging Metadata: {}", pageMeta.toString());

            // Get Member List by Room Session Ids
            for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                        .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setMemberStatus(MemberStatus.UNLOAD);
                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }
                    Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                        @Override
                        public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                            if(t1.getMemberType().equals(MemberType.LEADER)) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                }
                // Set Member List to Room Information Response
                response.setMemberList(memberInfoList);
                //log.debug("getRoomInfoList: {}", response.toString());
            }
            return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
        }
    }

    @Transactional
    public ApiResponse<RoomHistoryDetailInfoResponse> getRoomHistoryDetailInfo(String workspaceId, String sessionId) {
        log.info("ROOM HISTORY INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
        // Get Specific Room using Session ID
        RoomHistory roomHistory = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        // mapping data
        RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);

        // Get Member List by Room Session ID
        List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllBySessionId(resultResponse.getSessionId());

        // Mapping Member List Data to Member Information List
        List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                .collect(Collectors.toList());

        // find and get extra information from use-server using uuid
        if (!memberInfoList.isEmpty()) {
            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                log.debug("getUsers: " + userInfo.getData().toString());

                memberInfoResponse.setMemberStatus(MemberStatus.UNLOAD);
                memberInfoResponse.setEmail(userInfo.getData().getEmail());
                memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                memberInfoResponse.setLastName(userInfo.getData().getLastName());
                memberInfoResponse.setNickname(userInfo.getData().getNickname());
                memberInfoResponse.setProfile(userInfo.getData().getProfile());
            }
            Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                @Override
                public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                    if(t1.getMemberType().equals(MemberType.LEADER)) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
        // Set Member List to Room Detail Information Response
        resultResponse.setMemberList(memberInfoList);

        return new ApiResponse<>(resultResponse);
    }

    @Transactional
    public ApiResponse<ResultResponse> removeAllRoomHistory(String workspaceId, String userId) {
        log.info("ROOM HISTORY INFO DELETE ALL BY USER ID => [{}]", userId);
        ResultResponse resultResponse = new ResultResponse();
        List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findMemberHistoriesByWorkspaceIdAndUuid(workspaceId, userId);
        memberHistoryList.forEach(memberHistory -> {
            if(memberHistory.getRoomHistory() != null) {
                memberHistory.setRoomHistory(null);
                this.memberHistoryRepository.save(memberHistory);
            }
        });
        resultResponse.setResult(true);
        return new ApiResponse<>(resultResponse);
    }

    /*@Transactional
    public ApiResponse<Boolean> removeRoomHistory(String workspaceId, String sessionId, String userId) {
        log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", userId);
        MemberHistory memberHistory = this.memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, userId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND));
        //if(memberHistoryList.getSessionId().equals(sessionId)) {
        if(memberHistory.getUuid().equals(userId)) {
            memberHistory.setRoomHistory(null);
            this.memberHistoryRepository.save(memberHistory);
        }
        return new ApiResponse<>(true);
    }*/

    @Transactional
    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", roomHistoryDeleteRequest.getUuid());
        ResultResponse resultResponse = new ResultResponse();
        for (String sessionId: roomHistoryDeleteRequest.getSessionIdList()) {
            log.info("ROOM HISTORY INFO DELETE BY SESSION ID => [{}]", sessionId);
            MemberHistory memberHistory = this.memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, roomHistoryDeleteRequest.getUuid())
                    .orElseThrow(
                    () -> new RemoteServiceException(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND));

            if(memberHistory.getUuid().equals(roomHistoryDeleteRequest.getUuid())) {
                memberHistory.setRoomHistory(null);
                this.memberHistoryRepository.save(memberHistory);
            }
        }
        resultResponse.setResult(true);
        return new ApiResponse<>(resultResponse);
    }

    //
    public ApiResponse<WorkspaceMemberInfoListResponse> getMembers(String workspaceId, String filter, int page, int size) {
        log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
        ApiResponse<WorkspaceMemberInfoListResponse> response = this.workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, page, size);
        response.getData().getMemberInfoList().removeIf(memberInfoResponses ->
                Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty()
                || !Arrays.toString(memberInfoResponses.getLicenseProducts()).contains(LicenseConstants.PRODUCT_NAME));
        //log.info("getUsers: " + response.getData().getMemberInfoList());
        return new ApiResponse<>(response.getData());
    }
    /*public ApiResponse<UserInfoListResponse> getUsers(boolean paging, PageRequest pageRequest) {
        ApiResponse<UserInfoListResponse> response = this.userRestService.getUserInfoList(paging);
        log.debug("getUsers: " + response.getData().getUserInfoList());
        return new ApiResponse<>(response.getData());
    }*/
}
