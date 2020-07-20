package com.virnect.serviceserver.gateway.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.virnect.serviceserver.gateway.dao.MemberHistoryRepository;
import com.virnect.serviceserver.gateway.dao.MemberRepository;
import com.virnect.serviceserver.gateway.dao.RoomHistoryRepository;
import com.virnect.serviceserver.gateway.dao.RoomRepository;
import com.virnect.serviceserver.gateway.domain.*;
import com.virnect.serviceserver.gateway.dto.PageMetadataResponse;
import com.virnect.serviceserver.gateway.dto.SessionResponse;
import com.virnect.serviceserver.gateway.dto.SessionTokenResponse;
import com.virnect.serviceserver.gateway.dto.request.*;
import com.virnect.serviceserver.gateway.dto.response.*;
import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoResponse;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoResponse;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import com.virnect.serviceserver.gateway.global.constants.LicenseConstants;
import com.virnect.serviceserver.gateway.global.constants.ServiceConstants;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import com.virnect.serviceserver.gateway.infra.file.Default;
import com.virnect.serviceserver.gateway.service.LicenseRestService;
import com.virnect.serviceserver.gateway.service.RemoteServiceRestService;
import com.virnect.serviceserver.gateway.service.UserRestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final RemoteServiceRestService remoteServiceRestService;

    // feign client
    private final LicenseRestService licenseRestService;
    private final UserRestService userRestService;
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

    public ApiResponse<SessionListResponse> getSessionList(boolean webRtcStats) {
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
    }

    public ApiResponse<RoomInfoListResponse> getRoomInfoList(String search, boolean paging, Pageable pageable) {
        log.debug("getRoomInfoList");
        if(!paging) {
            List<Room> roomList;
            if(search == null) {
                // Get all room list
                roomList = this.roomRepository.findAll();
            } else {
                roomList = this.roomRepository.findByTitleIsContaining(search);
            }
            List<RoomInfoResponse> roomInfoList = roomList.stream()
                    .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                    .collect(Collectors.toList());

            // Get Member List by Room Session Ids
            for (RoomInfoResponse response: roomInfoList) {
                List<Member> memberList = this.memberRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberList.stream()
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

            return new ApiResponse<>(new RoomInfoListResponse(roomInfoList, null));
        } else {
            Page<Room> roomPage;

            if(search == null) {
                roomPage = this.roomRepository.findAll(pageable);
            } else {
                roomPage = this.roomRepository.findByTitleIsContaining(search, pageable);
            }

            List<RoomInfoResponse> roomInfoList = roomPage.stream()
                    .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                    .collect(Collectors.toList());

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(pageable.getPageNumber())
                    .currentSize(pageable.getPageSize())
                    .totalPage(roomPage.getTotalPages())
                    .totalElements(roomPage.getNumberOfElements())
                    .build();

            //roomInfoList.forEach(info -> log.info("{}", info));
            log.info("Paging Metadata: {}", pageMeta.toString());

            // Get Member List by Room Session Ids
            for (RoomInfoResponse response: roomInfoList) {
                List<Member> memberList = this.memberRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberList.stream()
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
        }
        // Mapping Room List Data to Room Information List
       /* List<RoomInfoResponse> roomInfoList = roomList.stream()
                .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                .collect(Collectors.toList());*/
    }

    public ApiResponse<RoomHistoryInfoListResponse> getRoomHistoryInfoList(String userId, final String search, boolean paging, Pageable pageable) {
        log.debug("getRoomHistoryInfoList");
        if (!paging) {
            // get all member history by uuid
            List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);
            List<RoomHistory> roomHistoryList = new ArrayList<>();
            if (search == null) {
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
                return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, null));

            } else {
                //first search title
                List<RoomHistory> roomSearch = this.roomHistoryRepository.findByTitleIsContaining(search);
                roomSearch.forEach(roomHistory -> {
                    memberHistoryList.forEach(memberHistory -> {
                        if (memberHistory.getRoomHistory() != null) {
                            if (memberHistory.getSessionId().equals(roomHistory.getSessionId())) {
                                roomHistoryList.add(memberHistory.getRoomHistory());
                            }
                        }
                    });
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
                            //secondly find search
                            ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                            if (userInfo.getData().getNickname().contains(search)) {
                                log.debug("getUsers: " + userInfo.getData().toString());
                                memberInfoResponse.setEmail(userInfo.getData().getEmail());
                                memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                                memberInfoResponse.setLastName(userInfo.getData().getLastName());
                                memberInfoResponse.setNickname(userInfo.getData().getNickname());
                                memberInfoResponse.setProfile(userInfo.getData().getProfile());
                            } else {
                                memberInfoList.remove(memberInfoResponse);
                            }
                        }
                        // Set Member List to Room History Information Response
                        response.setMemberList(memberInfoList);
                    }
                }
                return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, null));
            }
        } else {
            Page<RoomHistory> roomPage;
            // get all member history by uuid
            //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);
            //List<RoomHistory> roomHistoryList = new ArrayList<>();

            if(search == null) {
                roomPage = this.roomHistoryRepository.findAll(pageable);
            } else {
                roomPage = this.roomHistoryRepository.findByTitleIsContaining(search, pageable);
            }

            List<RoomHistoryInfoResponse> roomHistoryInfoList = roomPage.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(pageable.getPageNumber())
                    .currentSize(pageable.getPageSize())
                    .totalPage(roomPage.getTotalPages())
                    .totalElements(roomPage.getNumberOfElements())
                    .build();

            //roomInfoList.forEach(info -> log.info("{}", info));
            log.info("Paging Metadata: {}", pageMeta.toString());

            // Get Member List by Room Session Ids
            for (RoomHistoryInfoResponse response: roomHistoryInfoList) {
                List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                        .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
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
            return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
        }
    }

    public ApiResponse<RoomDetailInfoResponse> getRoomInfoBySessionId(String sessionId) {
        log.info("ROOM INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
        // Get Specific Room using Session ID
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

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
    public ApiResponse<RoomDetailInfoResponse> modifyRoomInfo(String sessionId, ModifyRoomInfoRequest modifyRoomInfoRequest, RoomProfileUpdateRequest roomProfileUpdateRequest) {
        log.info("ROOM INFO UPDATE BY SESSION ID => [{}]", sessionId);
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

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

    public ApiResponse<Boolean> inviteRoom(String sessionId, @Valid InviteRoomRequest inviteRoomRequest) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        if(!inviteRoomRequest.getParticipants().isEmpty()) {
            for (InviteRoomRequest.Participant participant : inviteRoomRequest.getParticipants()) {
                log.debug("getParticipants Id is {}", participant.toString());
                Member member = Member.builder()
                        .room(room)
                        .memberType(MemberType.WORKER)
                        .deviceType(DeviceType.UNKNOWN)
                        .memberStatus(MemberStatus.UNLOAD)
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
    }

    @Transactional
    public ApiResponse<Boolean> kickFromRoom(String sessionId, @Valid KickRoomRequest kickRoomRequest) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        if(room.getMembers().isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
        } else {
            for (Member member : room.getMembers()) {
                if(member.getUuid().equals(kickRoomRequest.getParticipantId())) {
                    this.memberRepository.delete(member);
                    return new ApiResponse<>(true);
                }
            }
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
        }
    }

    @Transactional
    public ApiResponse<Boolean>joinRoom(String sessionId, JoinRoomRequest joinRoomRequest) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        if(room.getMembers().size() >= ServiceConstants.PRODUCT_BASIC_MAX_USER) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
        } else {
            // set room members
            Member member = Member.builder()
                    .room(room)
                    .memberType(MemberType.WORKER)
                    .deviceType(DeviceType.UNKNOWN)
                    .memberStatus(MemberStatus.LOAD)
                    .uuid(joinRoomRequest.getUuid())
                    .email(joinRoomRequest.getEmail())
                    .sessionId(room.getSessionId())
                    .build();

            room.getMembers().add(member);
            roomRepository.save(room);
            return new ApiResponse<>(true);
        }
    }

    /**
     * This method is not for leader user
     * @param sessionId
     * @param userId
     * @return
     */
    @Transactional
    public ApiResponse<Boolean> leaveRoom(String sessionId, String userId) {
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));
        if(room.getMembers().isEmpty()) {
           throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
        } else {
            for (Member member : room.getMembers()) {
                if(member.getUuid().equals(userId)) {
                    this.memberRepository.delete(member);
                    return new ApiResponse<>(true);
                }
            }
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
        }
    }

    @Transactional
    public ApiResponse<Boolean> removeRoom(String sessionId) {
        log.info("ROOM INFO DELETE BY SESSION ID => [{}]", sessionId);
        Room room = roomRepository.findBySessionId(sessionId).orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        // Room and Member History 저장
        // Remote Room History Entity Create
        /*.mediaMode(room.getMediaMode())
                .recordingMode(room.getRecordingMode())
                .defaultOutputMode(room.getDefaultOutputMode())
                .defaultRecordingLayout(room.getDefaultRecordingLayout())
                .recording(room.isRecording())*/
        RoomHistory roomHistory = RoomHistory.builder()
                .sessionId(room.getSessionId())
                .title(room.getTitle())
                .description(room.getDescription())
                .profile(room.getProfile())
                .leaderId(room.getLeaderId())
                .workspaceId(room.getWorkspaceId())
                .build();

        roomHistory.setDurationTime("1000");
        // Set room member history
        // Get Member List by Room Session Ids
        List<Member> memberList = this.memberRepository.findAllBySessionId(room.getSessionId());
        // Mapping Member List Data to Member History List
        for (Member member : memberList) {
            MemberHistory memberHistory = MemberHistory.builder()
                    .roomHistory(roomHistory)
                    .uuid(member.getUuid())
                    .email(member.getEmail())
                    .memberType(member.getMemberType())
                    .deviceType(member.getDeviceType())
                    .sessionId(member.getSessionId())
                    .build();
            roomHistory.getMemberHistories().add(memberHistory);
        }
        roomHistoryRepository.save(roomHistory);

        //
        roomRepository.delete(room);
        return new ApiResponse<>(true);
    }

    @Transactional
    public ApiResponse<Boolean> removeAllRoomHistory(String userId) {
        log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", userId);
        //todo: do no delete all...
        //this.memberHistoryRepository.deleteAllByUuid(userId);
        List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);
        memberHistoryList.forEach(memberHistory -> {
            memberHistory.setRoomHistory(null);
            this.memberHistoryRepository.save(memberHistory);
        });
        return new ApiResponse<>(true);
    }

    @Transactional
    public ApiResponse<RoomResponse> createRoom(RoomRequest roomRequest,
                                                RoomProfileUpdateRequest roomProfileUpdateRequest,
                                                String session,
                                                String sessionToke) {
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
            sessionTokenResponse = objectMapper.readValue(sessionToke, SessionTokenResponse.class);
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
                    .deviceType(DeviceType.UNKNOWN)
                    .memberStatus(MemberStatus.UNLOAD)
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
                        .memberType(MemberType.WORKER)
                        .deviceType(DeviceType.UNKNOWN)
                        .memberStatus(MemberStatus.UNLOAD)
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
        return new ApiResponse<>(roomResponse);
    }

    //
    public ApiResponse<UserInfoListResponse> getUsers(String search, boolean paging, PageRequest pageRequest) {
        ApiResponse<UserInfoListResponse> response = this.userRestService.getUserInfoList(search, paging);
        log.debug("getUsers: " + response.getData().getUserInfoList());
        return new ApiResponse<>(response.getData());
    }
}
