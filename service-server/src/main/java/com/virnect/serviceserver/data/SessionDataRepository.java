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
import com.virnect.service.dto.feign.WorkspaceMemberInfoListResponse;
import com.virnect.service.dto.feign.WorkspaceMemberInfoResponse;
import com.virnect.service.dto.service.request.*;
import com.virnect.service.dto.service.response.*;
import com.virnect.service.error.ErrorCode;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.utils.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionDataRepository extends DataRepository {
    private static final String TAG = SessionDataRepository.class.getSimpleName();

    private final ObjectMapper objectMapper;

    private CoturnResponse setCoturnResponse(SessionType sessionType) {
        CoturnResponse coturnResponse = new CoturnResponse();
        switch (sessionType) {
            case OPEN: {
                List<String> urlList = config.remoteServiceProperties.getCoturnUrisStreaming();
                if(urlList.isEmpty()) {
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
            } break;
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

    public DataProcess<PushResponse> sendInviteMessage(InviteRoomResponse inviteRoomResponse) {
        return new RepoDecoder<Void, PushResponse>(RepoDecoderType.FETCH) {
            UserInfoResponse userInfoResponse;
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<PushResponse> invokeDataProcess() {

                fetchFromRepository(inviteRoomResponse.getLeaderId());

                pushMessageClient.setPush(
                        PushConstants.PUSH_SERVICE_REMOTE ,
                        PushConstants.SEND_PUSH_ROOM_INVITE ,
                        inviteRoomResponse.getWorkspaceId() ,
                        inviteRoomResponse.getLeaderId() ,
                        inviteRoomResponse.getParticipantIds());

                //set push message invite room contents
                ApiResponse<PushResponse> pushResponse = pushMessageClient.sendPushInvite(
                        inviteRoomResponse.getSessionId(),
                        inviteRoomResponse.getTitle(),
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

            private void fetchFromRepository(String userId) {
                DataProcess<UserInfoResponse> userInfo = checkUserValidation(userId);
                userInfoResponse = userInfo.getData();
            }
        }.asResponseData();
    }

    public DataProcess<PushResponse> sendEvictMessage(KickRoomResponse kickRoomResponse) {
        return new RepoDecoder<Void, PushResponse>(RepoDecoderType.NONE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<PushResponse> invokeDataProcess() {
                //if connection id cannot find, push message and just remove user
                pushMessageClient.setPush(
                        PushConstants.PUSH_SERVICE_REMOTE ,
                        PushConstants.SEND_PUSH_ROOM_EVICT ,
                        kickRoomResponse.getWorkspaceId() ,
                        kickRoomResponse.getLeaderId() ,
                        Arrays.asList(kickRoomResponse.getParticipantId()));

                ApiResponse<PushResponse> pushResponse = pushMessageClient.sendPushEvict();
                if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("push send message executed but not success");
                    log.info("push response: [code] {}", pushResponse.getCode());
                    log.info("push response: [message] {}", pushResponse.getMessage());
                } else {
                    log.info("push send message executed success {}", pushResponse.toString());
                }
                return new DataProcess<>(pushResponse.getData());
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

                    CoturnResponse coturnResponse = setCoturnResponse(room.getSessionProperty().getSessionType());
                    roomResponse.getCoturn().add(coturnResponse);

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

                        CoturnResponse coturnResponse = setCoturnResponse(room.getSessionProperty().getSessionType());
                        roomResponse.getCoturn().add(coturnResponse);
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

    public ApiResponse<RoomInfoListResponse> loadRoomPageList(
            String workspaceId,
            String userId,
            Pageable pageable
    ) {
        return new RepoDecoder<Page<Room>, RoomInfoListResponse>(RepoDecoderType.READ) {
            @Override
            Page<Room> loadFromDatabase() {
                return sessionService.getRoomPageList(workspaceId, userId, pageable);
            }

            @Override
            DataProcess<RoomInfoListResponse> invokeDataProcess() {
                //search all activated room
                Page<Room> roomPage = loadFromDatabase();

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
                    roomInfoResponse.setMemberList(memberInfoList);

                    roomInfoList.add(roomInfoResponse);
                }
                return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomInfoListResponse> searchRoomPageList(
            String workspaceId,
            String userId,
            String search,
            Pageable pageable) {
        return new RepoDecoder<Page<Room>, RoomInfoListResponse>(RepoDecoderType.READ) {
            List<MemberInfoResponse> memberInfoList = new ArrayList<>();

            private List<MemberInfoResponse> fetchFromRepository() {
                // fetch workspace member information
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

                log.info("fetchFromRepository::searchRoomPageList:: {}", feignResponse.getData().getPageMeta());

                for (MemberInfoResponse memberInfoResponse: memberInfoList) {
                    log.info("fetchFromRepository::searchRoomPageList:: {}", memberInfoResponse.toString());
                }

                return memberInfoList;
            }


            @Override
            Page<Room> loadFromDatabase() {
                List<String> userIds = new ArrayList<>();
                for (MemberInfoResponse memberInfo: memberInfoList) {
                    if(memberInfo.getUuid() == null || memberInfo.getUuid().isEmpty()) {
                        //if memberInfo is empty
                        log.info("loadFromDatabase::searchRoomPageList:: some member dose not have uuid");
                    } else {
                        userIds.add(memberInfo.getUuid());
                    }
                }
                if(userIds.isEmpty()) {
                    log.info("loadFromDatabase::searchRoomPageList::memberInfoList is empty can not find, search with room title");
                    return sessionService.getRoomPageList(workspaceId, userId, search, pageable);
                } else {
                    log.info("loadFromDatabase::searchRoomPageList::memberInfoList is not empty");
                    return sessionService.getRoomPageList(workspaceId, userId, userIds, search, pageable);
                }
            }

            @Override
            DataProcess<RoomInfoListResponse> invokeDataProcess() {
                memberInfoList = fetchFromRepository();

                //search all activated room
                Page<Room> roomPage = loadFromDatabase();

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
                for (Room room: roomPage.getContent()) {
                    RoomInfoResponse roomInfoResponse = modelMapper.map(room, RoomInfoResponse.class);
                    roomInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

                    // Mapping Member List Data to Member Information List
                    List<MemberInfoResponse> memberInfoList = room.getMembers().stream()
                            .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // find and get extra information from workspace-server using uuid
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                        log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                        WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                        memberInfoResponse.setRole(workspaceMemberData.getRole());
                        memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                        memberInfoResponse.setName(workspaceMemberData.getName());
                        memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                        memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                    }
                    roomInfoResponse.setMemberList(memberInfoList);

                    roomInfoList.add(roomInfoResponse);
                }
                return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomInfoListResponse> loadRoomList(
            String workspaceId,
            String userId,
            Pageable pageable
    ) {
        return new RepoDecoder<Page<Room>, RoomInfoListResponse>(RepoDecoderType.READ) {
            @Override
            Page<Room> loadFromDatabase() {
               return sessionService.getRoomPageList(workspaceId, userId, pageable);
            }

            @Override
            DataProcess<RoomInfoListResponse> invokeDataProcess() {
                Page<Room> roomPage = loadFromDatabase();

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
                    roomInfoResponse.setMemberList(memberInfoList);

                    roomInfoList.add(roomInfoResponse);
                }
                return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> loadRoom(String workspaceId, String sessionId) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.READ) {

            List<MemberInfoResponse> memberInfoList;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDetailInfoResponse> invokeDataProcess() {
                LogMessage.formedInfo(
                        TAG,
                        "invokeDataProcess",
                        "loadRoom",
                        "room info retrieve by session id",
                        sessionId
                );

                Room room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
                } else {
                    if (room.getRoomStatus() != RoomStatus.ACTIVE) {
                        return new DataProcess<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
                    } else {
                        // mapping data
                        RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(room, RoomDetailInfoResponse.class);
                        roomDetailInfoResponse.setSessionType(room.getSessionProperty().getSessionType());

                        // Get Member List by Room Session ID
                        // Mapping Member List Data to Member Information List
                        memberInfoList = sessionService.getMemberList(roomDetailInfoResponse.getSessionId())
                                .stream()
                                .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from workspace-server using uuid
                        fetchFromRepository();

                        // Set Member List to Room Detail Information Response
                        roomDetailInfoResponse.setMemberList(memberInfoList);
                        return new DataProcess<>(roomDetailInfoResponse);
                    }
                }
            }

            private void fetchFromRepository() {
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
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> updateRoom(
            String workspaceId,
            String sessionId,
            ModifyRoomInfoRequest modifyRoomInfoRequest) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.UPDATE) {
            Room room;
            List<MemberInfoResponse> memberInfoList;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDetailInfoResponse> invokeDataProcess() {
                LogMessage.formedInfo(
                        TAG,
                        "invokeDataProcess",
                        "updateRoom",
                        "room info retrieve by session id",
                        sessionId
                );

                room = loadFromDatabase();
                String userId = room.getLeaderId();
                if(room != null) {
                    if(userId.equals(modifyRoomInfoRequest.getUuid())) {
                        room = updateData();
                        Room updatedRoom = sessionService.updateRoom(room);

                        // mapping data
                        RoomDetailInfoResponse roomDetailInfoResponse = modelMapper.map(updatedRoom, RoomDetailInfoResponse.class);
                        roomDetailInfoResponse.setSessionType(updatedRoom.getSessionProperty().getSessionType());
                        // Get Member List by Room Session ID
                        // Mapping Member List Data to Member Information List
                        memberInfoList = sessionService.getMemberList(roomDetailInfoResponse.getSessionId())
                                .stream()
                                .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        fetchFromRepository();

                        // Set Member List to Room Detail Information Response
                        roomDetailInfoResponse.setMemberList(memberInfoList);
                        return new DataProcess<>(roomDetailInfoResponse);
                    } else {
                        return new DataProcess<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(new RoomDetailInfoResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }

            private Room updateData() {
                room.setTitle(modifyRoomInfoRequest.getTitle());
                room.setDescription(modifyRoomInfoRequest.getDescription());
                return room;
            }

            private void fetchFromRepository() {
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
        }.asApiResponse();
    }

    public ApiResponse<RoomDeleteResponse> removeRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Room, RoomDeleteResponse>(RepoDecoderType.DELETE) {
            Room room = null;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomDeleteResponse> invokeDataProcess() {
                room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                //check request user has valid permission
                if(!room.getLeaderId().equals(userId)) {
                    return new DataProcess<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                }

                for (Member member: room.getMembers()) {
                    if(member.getUuid().equals(room.getLeaderId()) && member.getMemberStatus().equals(MemberStatus.LOAD)) {
                        return new DataProcess<>(new RoomDeleteResponse(), ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
                    }
                }

                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", room.getMembers().size());

                setLogging();

                sessionService.deleteRoom(room);

                /*DataProcess<RoomDeleteResponse> dataProcess = null;
                try {
                    dataProcess = new DataProcess<>(RoomDeleteResponse.class);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }*/
                //log.info("ROOM INFO DELETE BY dataProcess => [{}]", dataProcess.data.toString());

                return new DataProcess<>(new RoomDeleteResponse(
                        sessionId,
                        true,
                        LocalDateTime.now()
                ));
            }

            private void setLogging() {
                setHistory();

                // check the same session id history room is already exist
                /*RoomHistory roomHistory = sessionService.getRoomHistory(room.getSessionId());
                if(roomHistory != null) {
                    log.info("FOUND THE SAME SESSION ID => [{}]", roomHistory.getSessionId());
                    derivedHistory(roomHistory);
                } else {
                    setHistory();
                }*/
            }

            private void derivedHistory(RoomHistory roomHistory) {

                roomHistory.setTitle(room.getTitle());
                roomHistory.setDescription(room.getDescription());
                roomHistory.setProfile(room.getProfile());
                roomHistory.setMaxUserCount(room.getMaxUserCount());
                roomHistory.setLicenseName(room.getLicenseName());

                // Remote Session Property Entity Create
                SessionProperty sessionProperty = room.getSessionProperty();
                SessionPropertyHistory sessionPropertyHistory = roomHistory.getSessionPropertyHistory();
                sessionPropertyHistory.setMediaMode(sessionProperty.getMediaMode());
                sessionPropertyHistory.setRecordingMode(sessionProperty.getRecordingMode());
                sessionPropertyHistory.setDefaultOutputMode(sessionProperty.getDefaultOutputMode());
                sessionPropertyHistory.setDefaultRecordingLayout(sessionProperty.getDefaultRecordingLayout());
                sessionPropertyHistory.setRecording(sessionProperty.isRecording());
                sessionPropertyHistory.setKeepalive(sessionProperty.isKeepalive());
                sessionPropertyHistory.setSessionType(sessionProperty.getSessionType());
                sessionPropertyHistory.setRoomHistory(roomHistory);

                roomHistory.setSessionPropertyHistory(sessionPropertyHistory);

                // Set room member history
                // Get Member history list and set room null
                List<MemberHistory> memberHistoryList = roomHistory.getMemberHistories();
                for (MemberHistory memberHistory: memberHistoryList) {
                    memberHistory.setHistoryDeleted(true);
                    sessionService.setMemberHistory(memberHistory);
                }

                // Get Member List by Room Session Ids
                // Mapping Member List Data to Member History List
                for (Member member : room.getMembers()) {
                    MemberHistory memberHistory = MemberHistory.builder()
                            .roomHistory(roomHistory)
                            .workspaceId(member.getWorkspaceId())
                            .uuid(member.getUuid())
                            .memberType(member.getMemberType())
                            .deviceType(member.getDeviceType())
                            .sessionId(member.getSessionId())
                            .startDate(member.getStartDate())
                            .endDate(member.getEndDate())
                            .durationSec(member.getDurationSec())
                            .build();

                    sessionService.setMemberHistory(memberHistory);
                    roomHistory.getMemberHistories().add(memberHistory);

                    //delete member
                    sessionService.deleteMember(member);
                }

                //set active time do not update active date
                //oldRoomHistory.setActiveDate(room.getActiveDate());
                //set un active  time
                LocalDateTime endTime = LocalDateTime.now();
                roomHistory.setUnactiveDate(endTime);

                //time diff seconds
                Duration duration = Duration.between(room.getActiveDate(), endTime);
                Long totalDuration = duration.getSeconds() + roomHistory.getDurationSec();
                roomHistory.setDurationSec(totalDuration);

                //save room history
                sessionService.setRoomHistory(roomHistory);
            }

            private void setHistory() {
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
            }
        }.asApiResponse();
    }

    /**
     * Prepare to join the room the user is....
     */
    /*@Deprecated
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
    }*/

    public ApiResponse<RoomResponse> joinRoom(String workspaceId, String sessionId, String sessionToken, JoinRoomRequest joinRoomRequest) {
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.READ) {
            SessionTokenResponse sessionTokenResponse = null;
            Room room;
            SessionType sessionType;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(new RoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                sessionType = room.getSessionProperty().getSessionType();

                preDataProcess();

                ErrorCode errorCode = getErrorStatus();
                if(errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(joinRoomRequest.getUuid()) && member.getMemberStatus().equals(MemberStatus.UNLOAD)) {
                            member.setMemberStatus(MemberStatus.LOADING);
                            sessionService.setMember(member);
                        }
                    }

                    RoomResponse roomResponse = new RoomResponse();
                    //not set session create at property
                    roomResponse.setSessionId(sessionId);
                    roomResponse.setToken(sessionTokenResponse.getToken());
                    roomResponse.setWss(ServiceServerApplication.wssUrl);

                    CoturnResponse coturnResponse = setCoturnResponse(room.getSessionProperty().getSessionType());
                    roomResponse.getCoturn().add(coturnResponse);

                    return new DataProcess<>(roomResponse);
                } else {
                    return new DataProcess<>(new RoomResponse(), errorCode);
                }
            }

            private void preDataProcess() {


                try {
                    sessionTokenResponse = objectMapper.readValue(sessionToken, SessionTokenResponse.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //assert sessionTokenResponse != null;
            }


            private ErrorCode getErrorStatus() {
                for (Member member : room.getMembers()) {
                    if (member.getUuid().equals(joinRoomRequest.getUuid())) {
                        MemberStatus memberStatus = member.getMemberStatus();
                        switch (memberStatus) {
                            case UNLOAD:
                                return ErrorCode.ERR_SUCCESS;
                            case LOAD:
                                return ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED;
                            case EVICTED:
                                break;
                        }
                    }
                }

                if(sessionType.equals(SessionType.OPEN)) {
                    return ErrorCode.ERR_SUCCESS;
                } else {
                    return ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED;
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> exitRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Room, ResultResponse>(RepoDecoderType.DELETE) {
            Member member = null;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                Room room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(new ResultResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
                } else {
                    for (Member participant : room.getMembers()) {
                        if(participant.getUuid().equals(userId)) {
                            member = participant;
                        }
                    }
                    if(member == null) {
                        return new DataProcess<>(new ResultResponse(), ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    } else {
                        ErrorCode errorCode = getErrorStatus();
                        if(errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                            sessionService.removeMember(room, member);
                            ResultResponse resultResponse = new ResultResponse();
                            resultResponse.setUserId(userId);
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                        } else {
                            return new DataProcess<>(new ResultResponse(), errorCode);
                        }
                    }
                }
            }

            private ErrorCode getErrorStatus() {
                if(member.getMemberType().equals(MemberType.LEADER)) {
                    return ErrorCode.ERR_ROOM_LEADER_INVALID_EXIT;
                } else if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                    return ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID;
                } else {
                    return ErrorCode.ERR_SUCCESS;
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<KickRoomResponse> kickFromRoom(String workspaceId, String sessionId, KickRoomRequest kickRoomRequest) {
        return new RepoDecoder<Room, KickRoomResponse>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);

            }

            @Override
            DataProcess<KickRoomResponse> invokeDataProcess() {
                Room room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                Member member = null;
                for (Member participant: room.getMembers()) {
                    if(participant.getUuid().equals(kickRoomRequest.getParticipantId())) {
                        member = participant;
                    }
                }

                 if(member == null) {
                    return new DataProcess<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                } else {
                    if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
                        return new DataProcess<>(new KickRoomResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    } else {
                        String connectionId = member.getConnectionId();

                        KickRoomResponse kickRoomResponse = new KickRoomResponse();
                        kickRoomResponse.setWorkspaceId(room.getWorkspaceId());
                        kickRoomResponse.setSessionId(room.getSessionId());
                        kickRoomResponse.setLeaderId(room.getLeaderId());
                        kickRoomResponse.setParticipantId(kickRoomRequest.getParticipantId());
                        kickRoomResponse.setConnectionId(connectionId);
                        //if connection id cannot find, push message and just remove user
                        if(connectionId == null || connectionId.isEmpty()) {

                            sessionService.updateMember(member, MemberStatus.EVICTED);
                            /*return new DataProcess<>(new ResultResponse(
                                    kickRoomRequest.getLeaderId(),
                                    true,
                                    LocalDateTime.now()
                            ));*/
                        }
                        return new DataProcess<>(kickRoomResponse);

                        /*if(sessionService.removeMember(room, kickRoomRequest.getParticipantId()).equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                            //resultResponse.setResult(true);
                        } else {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_PROCESS_FAIL);
                        }*/

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
                Room room = loadFromDatabase();
                if (room == null)
                    return new DataProcess<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);

                if (!room.getLeaderId().equals(inviteRoomRequest.getLeaderId())) {
                    return new DataProcess<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                } else {
                    List<Member> members = room.getMembers().stream()
                            .filter(member -> !member.getMemberStatus().equals(MemberStatus.EVICTED))
                            .collect(Collectors.toList());

                    //remove if member status is evicted
                    //room.getMembers().removeIf(member -> member.getRoom() == null);
                    //room.getMembers().removeIf(member -> member.getMemberStatus().equals(MemberStatus.EVICTED));

                    //check room member is exceeded limitation
                    if (members.size() + inviteRoomRequest.getParticipantIds().size() > room.getMaxUserCount()) {
                        return new DataProcess<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
                    }

                    //check invited member is already joined
                    List<String> userIds = members.stream()
                            .map(Member::getUuid)
                            .collect(Collectors.toList());
                    for (String participant : inviteRoomRequest.getParticipantIds()) {
                        if (userIds.contains(participant)) {
                            return new DataProcess<>(new InviteRoomResponse(), ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
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
                }
            }
        }.asApiResponse();
    }
}
