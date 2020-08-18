package com.virnect.serviceserver.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.data.ApiResponse;
import com.virnect.data.dao.*;
import com.virnect.data.dto.CoturnResponse;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.SessionResponse;
import com.virnect.data.dto.SessionTokenResponse;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.*;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rpc.ClientMetaData;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.feign.UserRestService;
import com.virnect.data.service.HistoryService;
import com.virnect.data.service.SessionService;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.core.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataRepository {
    private static final String TAG = DataRepository.class.getSimpleName();

    private final RemoteServiceConfig config;
    private SessionService sessionService;
    private final HistoryService historyService;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;

    //private final ImplementationTest implementationTest;
    @Autowired(required = true)
    @Qualifier(value = "sessionService")
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    public ApiResponse<RoomResponse> generateRoom(
            RoomRequest roomRequest,
            RoomProfileUpdateRequest roomProfileUpdateRequest,
            String session,
            String sessionToken
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
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

        SessionResponse finalSessionResponse = sessionResponse;
        SessionTokenResponse finalSessionTokenResponse = sessionTokenResponse;
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.CREATE) {
            @Override
            Room loadFromDatabase() {
                //ImplementationTest implementationTest = new ImplementationTest<com.virnect.data.dto.response.RoomInfoResponse>();
                //implementationTest.setData(new com.virnect.data.dto.response.RoomInfoListResponse());
                com.virnect.data.dto.response.RoomInfoResponse empty = new com.virnect.data.dto.response.RoomInfoResponse();
                return sessionService.createRoom(roomRequest, roomProfileUpdateRequest, finalSessionResponse);
            }

            @Override
            ApiResponse<RoomResponse> invokeDataProcess() {
                log.debug("createRoom: " + roomRequest.toString());
                RoomResponse roomResponse = new RoomResponse();
                //not set session create at property
                roomResponse.setSessionId(finalSessionResponse.getId());
                roomResponse.setToken(finalSessionTokenResponse.getToken());
                roomResponse.setWss(ServiceServerApplication.wssUrl);
                for (String coturnUrl: config.remoteServiceProperties.getCoturnUrisList()) {
                    CoturnResponse coturnResponse = new CoturnResponse();
                    coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                    coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                    coturnResponse.setUrl(coturnUrl);
                    roomResponse.getCoturn().add(coturnResponse);
                }

                return new ApiResponse<>(roomResponse);
            }
        }.asApiResponse();
    }

    public DataProcess<Void> generateRoomSession(String sessionId) {
        return new RepoDecoder<Room, Void>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<Void> invokeDataProcess() {
                sessionService.createSession(sessionId);
                return null;
            }
        }.asResponseData();
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
            ApiResponse<RoomInfoListResponse> invokeDataProcess() {
                if(!paging) {
                    //roomList = this.roomRepository.findByWorkspaceId(workspaceId);
                    List<RoomInfoResponse> roomInfoList = sessionService.getRoomList(workspaceId, userId)
                            .stream()
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
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(workspaceId, response.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if(!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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
                    Page<Member> memberPage = sessionService.getMemberList(workspaceId, userId, pageable);

                    List<RoomInfoResponse> roomInfoList = sessionService.getRoomList(memberPage)
                            .stream()
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
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(response.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if(!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> loadRoom(String workspaceId, String sessionId) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            ApiResponse<RoomDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                ApiResponse<RoomDetailInfoResponse> response = new ApiResponse<>();
                Room room = loadFromDatabase();
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
                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(resultResponse.getSessionId())
                                .stream()
                                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<ResultResponse> invokeDataProcess() {
                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", sessionId);
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, userId);

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

                    sessionService.removeRoom(room);

                    resultResponse.setResult(true);
                    return new ApiResponse<>(resultResponse);
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomDetailInfoResponse> updateRoom(String workspaceId, String sessionId, ModifyRoomInfoRequest modifyRoomInfoRequest, RoomProfileUpdateRequest roomProfileUpdateRequest) {
        return new RepoDecoder<Room, RoomDetailInfoResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            ApiResponse<RoomDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE BY SESSION ID => [{}]", sessionId);
                log.info("ROOM INFO UPDATE BY SESSION ID => [{}]", sessionId);
                Room room = loadFromDatabase();
                room = sessionService.modifyRoomInfo(room, modifyRoomInfoRequest, roomProfileUpdateRequest);

                // mapping data
                RoomDetailInfoResponse resultResponse = modelMapper.map(room, RoomDetailInfoResponse.class);

                // Get Member List by Room Session ID
                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = sessionService.getMemberList(resultResponse.getSessionId())
                        .stream()
                        .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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
        }.asApiResponse();
    }

    public ApiResponse<RoomResponse> joinRoom(String workspaceId, String sessionId, String sessionToken, JoinRoomRequest joinRoomRequest) {
        return new RepoDecoder<Room, RoomResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            ApiResponse<RoomResponse> invokeDataProcess() {
                ApiResponse<RoomResponse> response = new ApiResponse<>();
                Room room = loadFromDatabase();
                if (room == null) {
                    response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;
                }

                ErrorCode errorCode = sessionService.joinRoom(room, joinRoomRequest);
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
                        return new ApiResponse<>(roomResponse);
                    }
                    case ERR_ROOM_MEMBER_ALREADY_JOINED: {
                        response.setErrorResponseData(errorCode);
                        return response;
                    }
                    default: {
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                        return response;
                    }
                }
            }
        }.asApiResponse();
    }

    public DataProcess<Void> joinSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Void>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<Void> invokeDataProcess() {
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

                sessionService.joinSession(sessionId, participant.getParticipantPublicId(), clientMetaData);
                return null;
            }
        }.asResponseData();
    }

    public ApiResponse<ResultResponse> exitRoom(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<ResultResponse> invokeDataProcess() {
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, userId);

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
                        ErrorCode errorCode = sessionService.exitRoom(room, member);
                        if(errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                            return new ApiResponse<>(resultResponse);
                        } else {
                            response.setErrorResponseData(errorCode);
                            response.setData(response.getData());
                            return response;
                        }
                    }
                }
            }
        }.asApiResponse();
    }

    public DataProcess<Void> leaveSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Void>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<Void> invokeDataProcess() {
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

                sessionService.leaveSession(sessionId, clientMetaData);
                return null;
            }
        }.asResponseData();
    }

    public DataProcess<Void> destroySession(String sessionId) {
        return new RepoDecoder<Room, Void>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<Void> invokeDataProcess() {
                sessionService.destroySession(sessionId);
                return null;
            }
        }.asResponseData();
    }

    public ApiResponse<ResultResponse> kickFromRoom(String workspaceId, String sessionId, KickRoomRequest kickRoomRequest) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<ResultResponse> invokeDataProcess() {
                ResultResponse resultResponse = new ResultResponse();
                resultResponse.setResult(false);
                ApiResponse<ResultResponse> response = new ApiResponse<>(resultResponse);

                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, kickRoomRequest.getParticipantId());
                if(room == null) {
                    response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                } else if(member == null) {
                    response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                } else {
                    if (room.getMembers().isEmpty()) {
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                    } else if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    } else {
                        if(sessionService.removeMember(room, kickRoomRequest.getParticipantId()).equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                        } /*else {

                        }
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                        response.setData(response.getData());*/
                    }
                }
                return response;
            }
        }.asApiResponse();
    }

    public ApiResponse<MemberInfoResponse> loadMember(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Member, MemberInfoResponse>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<MemberInfoResponse> invokeDataProcess() {
                Member member = sessionService.getMember(workspaceId, sessionId, userId);
                MemberInfoResponse resultResponse;
                // mapping data
                resultResponse = modelMapper.map(member, MemberInfoResponse.class);
                return new ApiResponse<>(resultResponse);
            }
        }.asApiResponse();
    }

    //=========================================================
    public ApiResponse<RoomHistoryInfoListResponse> loadRoomHistoryInfoList(
            String workspaceId,
            String userId,
            boolean paging,
            Pageable pageable) {
        return new RepoDecoder<MemberHistory, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            @Override
            MemberHistory loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<RoomHistoryInfoListResponse> invokeDataProcess() {
                if (!paging) {
                    // get all member history by uuid
                    //List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(workspaceId, userId);

                    PageRequest pageRequest = new PageRequest();
                    Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageRequest.of());

                    List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberPage.getContent().forEach(memberHistory -> {
                        //if (memberHistory.getRoomHistory() != null) {
                        roomHistoryList.add(memberHistory.getRoomHistory());
                        //}
                    });

                    List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                            .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                            .collect(Collectors.toList());

                    // find specific member has room history
                    /*List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberHistoryList.forEach(memberHistory -> {
                        if (memberHistory.getRoomHistory() != null) {
                            roomHistoryList.add(memberHistory.getRoomHistory());
                        }
                    });

                    List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                            .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(0)
                            .currentSize(0)
                            .numberOfElements(memberPage.getNumberOfElements())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getTotalElements())
                            .last(memberPage.isLast())
                            .build();

                    // Get Member List by Room Session Ids
                    for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                        List<MemberHistory> memberList = historyService.getMemberHistoryList(response.getSessionId());

                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = memberList.stream()
                                .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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
                                    if (t1.getMemberType().equals(MemberType.LEADER)) {
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
                    Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageable);
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
                            .numberOfElements(memberPage.getNumberOfElements())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getTotalElements())
                            .last(memberPage.isLast())
                            .build();

                    //roomInfoList.forEach(info -> log.info("{}", info));
                    log.info("Paging Metadata: {}", pageMeta.toString());

                    // Get Member List by Room Session Ids
                    for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                        List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(response.getSessionId());

                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                                .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                                ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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
                                    if (t1.getMemberType().equals(MemberType.LEADER)) {
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
        }.asApiResponse();
    }

    public ApiResponse<RoomHistoryDetailInfoResponse> loadRoomHistoryDetail(String workspaceId, String sessionId) {
        return new RepoDecoder<RoomHistory, RoomHistoryDetailInfoResponse>(RepoDecoderType.READ) {
            @Override
            RoomHistory loadFromDatabase() {
                return historyService.getRoomHistory(workspaceId, sessionId);
            }

            @Override
            ApiResponse<RoomHistoryDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                ApiResponse<RoomHistoryDetailInfoResponse> response = new ApiResponse<>();
                RoomHistory roomHistory = loadFromDatabase();
                if(roomHistory == null) {
                    response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;
                } else {
                    // mapping data
                    RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);

                    // Get Member List by Room Session ID
                    // Mapping Member List Data to Member Information List
                    List<MemberInfoResponse> memberInfoList = historyService.getMemberHistoryList(resultResponse.getSessionId())
                            .stream()
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // find and get extra information from use-server using uuid
                    if (!memberInfoList.isEmpty()) {
                        for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                            ApiResponse<UserInfoResponse> userInfo = userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
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

            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            ApiResponse<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE ALL BY USER ID => [{}]", userId);
                ResultResponse resultResponse = new ResultResponse();
                List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(workspaceId, userId);
                historyService.removeRoomHistory(memberHistoryList);
                resultResponse.setResult(true);
                return new ApiResponse<>(resultResponse);
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }
            @Override
            ApiResponse<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", roomHistoryDeleteRequest.getUuid());
                ResultResponse resultResponse = new ResultResponse();
                //resultResponse.setResult(false);
                //ApiResponse<ResultResponse> apiResponse = new ApiResponse<>(resultResponse);
                for (String sessionId: roomHistoryDeleteRequest.getSessionIdList()) {
                    log.info("ROOM HISTORY INFO DELETE BY SESSION ID => [{}]", sessionId);
                    MemberHistory memberHistory = historyService.getMemberHistory(workspaceId, sessionId, roomHistoryDeleteRequest.getUuid());
                    if(memberHistory == null) {
                        log.info("ROOM HISTORY INFO DELETE BUT MEMBER HISTORY DATA IS NULL BY SESSION ID => [{}]", sessionId);
                        //apiResponse.getData().setResult(false);
                        //apiResponse.setErrorResponseData(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND);
                    } else {
                        if (memberHistory.getUuid().equals(roomHistoryDeleteRequest.getUuid())) {
                            historyService.removeRoomHistory(memberHistory);
                        }
                    }
                }
                resultResponse.setResult(true);
                return new ApiResponse<>(resultResponse);
            }
        }.asApiResponse();
    }






    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}
