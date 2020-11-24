package com.virnect.serviceserver.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.data.dao.*;
import com.virnect.service.ApiResponse;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.constraint.PushConstants;
import com.virnect.service.dto.*;
import com.virnect.service.dto.feign.PushResponse;
import com.virnect.service.dto.feign.UserInfoResponse;
import com.virnect.service.dto.feign.WorkspaceMemberInfoResponse;
import com.virnect.service.dto.service.request.*;
import com.virnect.service.dto.service.response.*;
import com.virnect.service.error.ErrorCode;
import com.virnect.serviceserver.ServiceServerApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionDataRepository extends DataRepository {
    private static final String TAG = SessionDataRepository.class.getSimpleName();

    private final ObjectMapper objectMapper;

    public DataProcess<PushResponse> sendSessionCreate(String sessionId) {
        return new RepoDecoder<Room, PushResponse>(RepoDecoderType.READ) {
            UserInfoResponse userInfoResponse;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<PushResponse> invokeDataProcess() {
                Room room = loadFromDatabase();
                if(room != null) {
                    SessionProperty sessionProperty = room.getSessionProperty();
                    String userId = sessionProperty.getPublisherId();
                    List<String> targetIds = room.getMembers().stream()
                            .map(Member::getUuid)
                            .filter(s -> !s.equals(userId))
                            .collect(Collectors.toList());

                    log.info("sendSessionCreate:: userId : {}", userId);
                    for (String id: targetIds) {
                        log.info("sendSessionCreate:: targetIds : {}", id);
                    }

                    fetchFromRepository(userId);

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
                            userInfoResponse.getProfile());
                    if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                        log.info("push send message executed but not success");
                        log.info("push response: [code] {}", pushResponse.getCode());
                        log.info("push response: [message] {}", pushResponse.getMessage());
                    } else {
                        log.info("push send message executed success {}", pushResponse.toString());
                    }
                    return new DataProcess<>(pushResponse.getData());
                }
                return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
            }

            private void fetchFromRepository(String userId) {
                DataProcess<UserInfoResponse> userInfo = checkUserValidation(userId);
                userInfoResponse = userInfo.getData();
            }
        }.asResponseData();
    }

    public ApiResponse<RoomResponse> generateRoom(
            RoomRequest roomRequest,
            LicenseItem licenseItem,
            String publisherId,
            String session,
            String sessionToken
    ) {
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.CREATE) {
            SessionResponse sessionResponse = null;
            SessionTokenResponse sessionTokenResponse = null;

            private void preDataProcess() {
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
            }

            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                preDataProcess();

                Room room = saveData();

                if(sessionService.createRoom(room) != null) {
                    RoomResponse roomResponse = new RoomResponse();
                    //not set session create at property
                    roomResponse.setSessionId(sessionResponse.getId());
                    roomResponse.setToken(sessionTokenResponse.getToken());
                    roomResponse.setWss(ServiceServerApplication.wssUrl);
                    if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                        for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisSteaming()) {
                            CoturnResponse coturnResponse = new CoturnResponse();
                            coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                            coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                            coturnResponse.setUrl(coturnUrl);
                            roomResponse.getCoturn().add(coturnResponse);
                        }
                    } else {
                        for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
                            CoturnResponse coturnResponse = new CoturnResponse();
                            coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                            coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                            coturnResponse.setUrl(coturnUrl);
                            roomResponse.getCoturn().add(coturnResponse);
                        }
                    }
                    return new DataProcess<>(roomResponse);
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
                }
            }

            private void setMember(Room room) {
                // set room members
                if(!roomRequest.getLeaderId().isEmpty()) {
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

                if(!roomRequest.getParticipantIds().isEmpty()) {
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
            }

            private Room saveData() {
                // Remote Room Entity Create
                Room room = Room.builder()
                        .sessionId(sessionResponse.getId())
                        .title(roomRequest.getTitle())
                        .description(roomRequest.getDescription())
                        .leaderId(roomRequest.getLeaderId())
                        .workspaceId(roomRequest.getWorkspaceId())
                        .maxUserCount(licenseItem.getUserCapacity())
                        .licenseName(licenseItem.name())
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

                setMember(room);

                return room;
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomResponse> generateRoom(
            String preSessionId,
            RoomRequest roomRequest,
            LicenseItem licenseItem,
            String publisherId,
            String session,
            String sessionToken
    ) {
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.CREATE) {
            SessionResponse sessionResponse = null;
            SessionTokenResponse sessionTokenResponse = null;
            String profile;

            private void preDataProcess() {
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
            }

            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                preDataProcess();

                RoomHistory roomHistory = historyService.getRoomHistory(roomRequest.getWorkspaceId(), preSessionId);
                if(roomHistory == null) {
                    return new DataProcess<>(ErrorCode.ERR_HISTORY_ROOM_NOT_FOUND);
                } else {
                    log.info("REDIAL ROOM::#generateRoom::re-generate room by history::session_id => [{}]", preSessionId);
                    profile = roomHistory.getProfile();
                    Room room = saveData();
                    if(sessionService.createRoom(room) != null) {
                        RoomResponse roomResponse = new RoomResponse();
                        //not set session create at property
                        roomResponse.setSessionId(sessionResponse.getId());
                        roomResponse.setToken(sessionTokenResponse.getToken());
                        roomResponse.setWss(ServiceServerApplication.wssUrl);
                        if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                            for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisSteaming()) {
                                CoturnResponse coturnResponse = new CoturnResponse();
                                coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                                coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                                coturnResponse.setUrl(coturnUrl);
                                roomResponse.getCoturn().add(coturnResponse);
                            }
                        } else {
                            for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
                                CoturnResponse coturnResponse = new CoturnResponse();
                                coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                                coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                                coturnResponse.setUrl(coturnUrl);
                                roomResponse.getCoturn().add(coturnResponse);
                            }
                        }
                        return new DataProcess<>(roomResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
                    }
                }
            }

            private void setMember(Room room) {
                // set room members
                if(!roomRequest.getLeaderId().isEmpty()) {
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

                if(!roomRequest.getParticipantIds().isEmpty()) {
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
            }

            private Room saveData() {
                // Remote Room Entity Create
                Room room = Room.builder()
                        .sessionId(sessionResponse.getId())
                        .title(roomRequest.getTitle())
                        .description(roomRequest.getDescription())
                        .leaderId(roomRequest.getLeaderId())
                        .workspaceId(roomRequest.getWorkspaceId())
                        .maxUserCount(licenseItem.getUserCapacity())
                        .licenseName(licenseItem.name())
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

                setMember(room);

                return room;
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomInfoListResponse> loadRoomList(
            String workspaceId,
            String userId,
            boolean paging,
            Pageable pageable
    ) {
        return new RepoDecoder<Member, RoomInfoListResponse>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomInfoListResponse> invokeDataProcess() {
                if(!paging) {
                    log.info("INVOKE METHOD : loadRoomList :: not paging {} {}", workspaceId, userId);
                    //first add private room
                    List<Room> roomList = sessionService.getRoomList(workspaceId, userId);
                    //send add open room
                    roomList.addAll(sessionService.getRoomList(workspaceId));

                    List<RoomInfoResponse> roomInfoList = new ArrayList<>();
                    for (Room room: roomList) {
                        log.info("INVOKE METHOD : roomList ::");
                        RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
                        roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());
                        roomInfoList.add(roomInfoResponse);
                    }

                    /*List<RoomInfoResponse> roomInfoList = sessionService.getRoomList(workspaceId, userId)
                            .stream()
                            .map(room -> modelMapper.map(room, RoomInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // Page Metadata Empty
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(0)
                            .currentSize(0)
                            .totalPage(0)
                            .totalElements(0)
                            .build();

                    // Get Member List by Room Session Ids
                    for (RoomInfoResponse response: roomInfoList) {
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(workspaceId, response.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from workspace-server using uuid
                        if(!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
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
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                        //log.debug("getRoomInfoList: {}", response.toString());
                    }
                    return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
                } else {
                    log.info("INVOKE METHOD : loadRoomList :: paging");

                    Page<Member> memberPage = sessionService.getMemberList(workspaceId, userId, pageable);

                    //first add private room
                    List<Room> roomList = sessionService.getRoomList(memberPage);
                    //send add open room
                    roomList.addAll(sessionService.getRoomList(workspaceId));

                    List<RoomInfoResponse> roomInfoList = new ArrayList<>();
                    for (Room room: roomList) {
                        RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
                        roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());
                        roomInfoList.add(roomInfoResponse);
                    }

                    /*List<RoomInfoResponse> roomInfoList = sessionService.getRoomList(memberPage)
                            .stream()
                            .map(room -> modelMapper.map(room, RoomInfoResponse.class))
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
                    for (RoomInfoResponse response: roomInfoList) {
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(response.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());
                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from workspace-server using uuid
                        if(!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
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
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                    }
                    return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> loadRoom(String workspaceId, String sessionId) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                //ApiResponse<RoomDetailInfoResponse> response = new ApiResponse<>();
                Room room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else {
                    if (room.getRoomStatus() != RoomStatus.ACTIVE) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
                    } else {
                        RoomDetailInfoResponse resultResponse;
                        // mapping data
                        //RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        resultResponse.setSessionType(room.getSessionProperty().getSessionType());

                        // Get Member List by Room Session ID
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(resultResponse.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        //remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from workspace-server using uuid
                        //if (!memberInfoList.isEmpty()) {
                        for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                            ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
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
                        //}
                        // Set Member List to Room Detail Information Response
                        resultResponse.setMemberList(memberInfoList);
                        return new DataProcess<>(resultResponse);
                    }
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> updateRoom(
            String workspaceId,
            String sessionId,
            ModifyRoomInfoRequest modifyRoomInfoRequest) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                Room room = loadFromDatabase();
                room = sessionService.updateRoom(room, modifyRoomInfoRequest);
                if(room != null) {
                    if(room.getLeaderId().equals(modifyRoomInfoRequest.getUuid())) {
                        // mapping data
                        RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        resultResponse.setSessionType(room.getSessionProperty().getSessionType());
                        // Get Member List by Room Session ID
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(resultResponse.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                                log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                                //todo://user infomation does not have role and role id change to workspace member info
                                WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                                memberInfoResponse.setRole(workspaceMemberData.getRole());
                                //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                                memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                                memberInfoResponse.setName(workspaceMemberData.getName());
                                memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                                memberInfoResponse.setProfile(workspaceMemberData.getProfile());

                                /*ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                                log.debug("getUsers: " + userInfo.getData().toString());

                                memberInfoResponse.setEmail(userInfo.getData().getEmail());
                                memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                                memberInfoResponse.setLastName(userInfo.getData().getLastName());
                                memberInfoResponse.setNickname(userInfo.getData().getNickname());
                                memberInfoResponse.setProfile(userInfo.getData().getProfile());*/
                            }
                        }
                        // Set Member List to Room Detail Information Response
                        resultResponse.setMemberList(memberInfoList);
                        return new DataProcess<>(resultResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDeleteResponse> removeRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Room, RoomDeleteResponse>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", sessionId);
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDeleteResponse> invokeDataProcess() {
                Room room = loadFromDatabase();
                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", room.getMembers().size());
                DataProcess<RoomDeleteResponse> dataProcess = null;
                try {
                    dataProcess = new DataProcess<>(RoomDeleteResponse.class);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                log.info("ROOM INFO DELETE BY dataProcess => [{}]", dataProcess.data.toString());

                if(room == null) {
                    dataProcess.setErrorCode(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return dataProcess;
                }

                //check request user has valid permission
                if(!room.getLeaderId().equals(userId)) {
                    dataProcess.setErrorCode(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    return dataProcess;
                }

                //List<Member> members = room.getMembers();
                List<Member> members = sessionService.getMemberList(room.getWorkspaceId(), room.getSessionId());
                for (Member member: members) {
                    log.info("ROOM INFO DELETE BY dataProcess => [{}]", member.getUuid());
                    if(member.getUuid().equals(room.getLeaderId())
                            && member.getMemberStatus().equals(MemberStatus.LOAD)) {
                        dataProcess.setErrorCode(ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED);
                        return dataProcess;
                    }
                }

                sessionService.removeRoom(room);
                //sessionService.removeRoom(workspaceId, sessionId);
                return new DataProcess<>(new RoomDeleteResponse(
                        sessionId,
                        true,
                        LocalDateTime.now()
                ));
            }
        }.asApiResponse();
    }

    /**
     * Prepare to join the room the user is....
     * @param workspaceId
     * @param sessionId
     * @param userId
     * @return
     */
    public DataProcess<Boolean> prepareJoinRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                Room room = loadFromDatabase();
                if (room == null) {
                    return new DataProcess<>(false, ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                if (room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(userId)) {
                            log.info("open room has member Id is {}", member.getUuid());
                            if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                                return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                            }
                        }
                    }
                    log.info("open room has no member Id is {}", userId);
                    return new DataProcess<>(true);
                } else {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(userId)) {
                            log.info("private room has member Id is {}", member.getUuid());
                            if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                                return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                            } else {
                                return new DataProcess<>(true);
                            }
                        }
                    }
                    return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                }
            }
        }.asResponseData();
    }

    public ApiResponse<RoomResponse> joinRoom(String workspaceId, String sessionId, String sessionToken, JoinRoomRequest joinRoomRequest) {
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                //ApiResponse<RoomResponse> response = new ApiResponse<>();
                /*Room room = loadFromDatabase();
                if (room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    *//*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;*//*
                }*/
                Room room = loadFromDatabase();
                sessionService.joinRoom(room, joinRoomRequest);

                SessionTokenResponse sessionTokenResponse = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                assert sessionTokenResponse != null;

                RoomResponse roomResponse = new RoomResponse();
                //not set session create at property
                roomResponse.setSessionId(sessionId);
                roomResponse.setToken(sessionTokenResponse.getToken());

                roomResponse.setWss(ServiceServerApplication.wssUrl);

                if(room.getSessionProperty().getSessionType().equals(SessionType.OPEN)) {
                    for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisSteaming()) {
                        CoturnResponse coturnResponse = new CoturnResponse();
                        coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                        coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                        coturnResponse.setUrl(coturnUrl);
                        roomResponse.getCoturn().add(coturnResponse);
                    }
                } else {
                    for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisConference()) {
                        CoturnResponse coturnResponse = new CoturnResponse();
                        coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                        coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                        coturnResponse.setUrl(coturnUrl);
                        roomResponse.getCoturn().add(coturnResponse);
                    }
                }
                return new DataProcess<>(roomResponse);

                /*ErrorCode errorCode = sessionService.joinRoom(room, joinRoomRequest);
                switch (errorCode) {
                    case ERR_SUCCESS: {
                        SessionTokenResponse sessionTokenResponse = null;
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        assert sessionTokenResponse != null;

                        RoomResponse roomResponse = new RoomResponse();
                        //not set session create at property
                        roomResponse.setSessionId(sessionId);
                        roomResponse.setToken(sessionTokenResponse.getToken());

                        roomResponse.setWss(ServiceServerApplication.wssUrl);
                        for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisList()) {
                            CoturnResponse coturnResponse = new CoturnResponse();
                            coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                            coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                            coturnResponse.setUrl(coturnUrl);
                            roomResponse.getCoturn().add(coturnResponse);
                        }
                        return new DataProcess<>(roomResponse);
                    }
                    case ERR_ROOM_MEMBER_ALREADY_JOINED: {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                        //response.setErrorResponseData(errorCode);
                        //return response;
                    }
                    default: {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                        //return response;
                    }
                }*/
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> exitRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, userId);

                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else if(member == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                } else {
                    if(room.getMembers().isEmpty()) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                    } else {
                        ErrorCode errorCode = sessionService.exitRoom(room, member);
                        if(errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                            ResultResponse resultResponse = new ResultResponse();
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                        } else {
                            return new DataProcess<>(errorCode);
                        }
                    }
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> kickFromRoom(String workspaceId, String sessionId, KickRoomRequest kickRoomRequest) {
        return new RepoDecoder<Member, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Member loadFromDatabase() {
                return sessionService.getMember(workspaceId, sessionId, kickRoomRequest.getParticipantId());
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                ResultResponse resultResponse = new ResultResponse();
                resultResponse.setResult(false);
                //DataProcess<ResultResponse> response = new DataProcess<>(resultResponse);
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = loadFromDatabase();
                //Member member = sessionService.getMember(workspaceId, sessionId, kickRoomRequest.getParticipantId());
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    //response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else if(member == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                } else {
                    if (room.getMembers().isEmpty()) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                    } else if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    } else {
                        sessionService.updateMember(member, MemberStatus.EVICTED);
                        if(sessionService.removeMember(room, kickRoomRequest.getParticipantId()).equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                            //resultResponse.setResult(true);
                        } else {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_PROCESS_FAIL);
                        }
                    }
                }
                //return new DataProcess<>(resultResponse);
                //return response;
            }
        }.asApiResponse();
    }

    public ApiResponse<InviteRoomResponse> inviteMember(String workspaceId, String sessionId, InviteRoomRequest inviteRoomRequest) {
        return new RepoDecoder<Room, InviteRoomResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<InviteRoomResponse> invokeDataProcess() {
                log.info("ROOM INVITE MEMBER UPDATE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                Room room = loadFromDatabase();
                if(room != null) {
                    if(room.getLeaderId().equals(inviteRoomRequest.getLeaderId())) {
                        //remove if member status is evicted
                        //room.getMembers().removeIf(member -> member.getRoom() == null);
                        room.getMembers().removeIf(member -> member.getMemberStatus().equals(MemberStatus.EVICTED));

                        //check room member is exceeded limitation
                        if(room.getMembers().size() + inviteRoomRequest.getParticipantIds().size() > room.getMaxUserCount()) {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
                        }

                        //check invited member is already joined
                        for(String participant : inviteRoomRequest.getParticipantIds()) {
                            for(Member member: room.getMembers()) {
                                if(participant.equals(member.getUuid()) && !member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                                }
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

                        return new DataProcess<>(response);

                        //sessionService.updateRoom(room, inviteRoomRequest.getParticipantIds());
                        /*for(String participant : inviteRoomRequest.getParticipantIds()) {
                            sessionService.createOrUpdateMember(room, participant);
                        }*/
                        /*List<String> additionalIds = new ArrayList<>();
                        for(String participant : inviteRoomRequest.getParticipantIds()) {
                            for(Member member: room.getMembers()) {
                                if(participant.equals(member.getUuid()) && member.getMemberStatus().equals(MemberStatus.EVICTED)) {
                                    additionalIds.add();
                                } else {
                                    additionalIds.add();
                                }
                            }
                            sessionService.createOrUpdateMember(room, participant);
                        }*/

                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }
}
