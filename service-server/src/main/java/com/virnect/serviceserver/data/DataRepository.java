package com.virnect.serviceserver.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.serviceserver.api.ApiResponse;
import com.virnect.serviceserver.dao.*;
import com.virnect.serviceserver.dto.CoturnResponse;
import com.virnect.serviceserver.dto.PageMetadataResponse;
import com.virnect.serviceserver.dto.SessionResponse;
import com.virnect.serviceserver.dto.SessionTokenResponse;
import com.virnect.serviceserver.dto.request.*;
import com.virnect.serviceserver.dto.response.*;
import com.virnect.serviceserver.dto.rest.UserInfoResponse;
import com.virnect.serviceserver.dto.rpc.ClientMetaData;
import com.virnect.serviceserver.error.ErrorCode;
import com.virnect.serviceserver.feign.UserRestService;
import com.virnect.serviceserver.service.SessionService;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.core.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataRepository {
    private static final String TAG = DataRepository.class.getSimpleName();

    private final RemoteServiceConfig config;
    private final SessionService sessionService;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;

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

                sessionService.joinSession(sessionId, clientMetaData);
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
                    response.setData(response.getData());
                } else if(member == null) {
                    response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    response.setData(response.getData());
                } else {
                    if (room.getMembers().isEmpty()) {
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                        response.setData(response.getData());
                    } else if (!room.getLeaderId().equals(kickRoomRequest.getLeaderId())) {
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                        response.setData(response.getData());
                    } else {
                        if(sessionService.removeMember(room, kickRoomRequest.getParticipantId()).equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                        } else {

                        }
                        response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                        response.setData(response.getData());
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

    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}
