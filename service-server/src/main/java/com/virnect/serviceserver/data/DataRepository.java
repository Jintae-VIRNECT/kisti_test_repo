package com.virnect.serviceserver.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.data.ApiResponse;
import com.virnect.data.constraint.LicenseConstants;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.dao.*;
import com.virnect.data.dto.CoturnResponse;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.SessionResponse;
import com.virnect.data.dto.SessionTokenResponse;
import com.virnect.data.dto.feign.LicenseInfoListResponse;
import com.virnect.data.dto.feign.LicenseInfoResponse;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.*;
import com.virnect.data.dto.feign.UserInfoResponse;
import com.virnect.data.dto.rpc.ClientMetaData;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.feign.service.LicenseRestService;
import com.virnect.data.feign.service.UserRestService;
import com.virnect.data.service.HistoryService;
import com.virnect.data.service.SessionService;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.core.Participant;
import com.virnect.serviceserver.infra.file.Default;
import com.virnect.serviceserver.infra.file.LocalFileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final LicenseRestService licenseRestService;
    private final ModelMapper modelMapper;
    //
    private final LocalFileUploadService localFileUploadService;

    //private final ImplementationTest implementationTest;
    @Autowired(required = true)
    @Qualifier(value = "sessionService")
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    public ApiResponse<RoomResponse> generateRoom(
            RoomRequest roomRequest,
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
                return null;
            }

            @Override
            DataProcess<RoomResponse> invokeDataProcess() {
                log.info("createRoom: " + roomRequest.toString());
                ApiResponse<LicenseInfoListResponse> licenseValidation = licenseRestService.getUserLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
                if(licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("licenseValidation code is not ok");
                    return new DataProcess<>(licenseValidation.getCode(), licenseValidation.getMessage());
                }

                LicenseInfoResponse currentLicense = null;
                for (LicenseInfoResponse licenseInfoResponse: licenseValidation.getData().getLicenseInfoList()) {
                    if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
                        currentLicense = licenseInfoResponse;
                    }
                }

                LicenseItem licenseItem = null;
                if(currentLicense != null) {
                    if (currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
                        if (currentLicense.getLicenseType().contains(LicenseConstants.LICENSE_BASIC)) {
                            licenseItem = LicenseItem.ITEM_BASIC;
                        }
                        if (currentLicense.getLicenseType().contains(LicenseConstants.LICENSE_BUSINESS)) {
                            licenseItem = LicenseItem.ITEM_BUSINESS;
                        }
                        if (currentLicense.getLicenseType().contains(LicenseConstants.LICENSE_PERMANENT)) {
                            licenseItem = LicenseItem.ITEM_PERMANENT;
                        }
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
                }

                if(licenseItem == null) {
                    return new DataProcess<>(ErrorCode.ERR_LICENSE_TYPE_VALIDITY);
                }

                Room room = sessionService.createRoom(roomRequest, licenseItem, finalSessionResponse);
                if(room != null) {
                    RoomResponse roomResponse = new RoomResponse();
                    //not set session create at property
                    roomResponse.setSessionId(finalSessionResponse.getId());
                    roomResponse.setToken(finalSessionTokenResponse.getToken());
                    roomResponse.setWss(ServiceServerApplication.wssUrl);
                    for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisList()) {
                        CoturnResponse coturnResponse = new CoturnResponse();
                        coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                        coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                        coturnResponse.setUrl(coturnUrl);
                        roomResponse.getCoturn().add(coturnResponse);
                    }
                    return new DataProcess<>(roomResponse);
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_CREATE_FAIL);
                }
            }
        }.asApiResponse();
    }

    public DataProcess<Boolean> generateRoomSession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                log.info("GENERATE ROOM SESSION RETRIEVE BY SESSION ID => [{}]", sessionId);
                sessionService.createSession(sessionId);
                return new DataProcess<>(true);
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
            DataProcess<RoomInfoListResponse> invokeDataProcess() {
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
                    return new DataProcess<>(new RoomInfoListResponse(roomInfoList, pageMeta));
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
                    //response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    //return response;
                } else {
                    if (room.getRoomStatus() != RoomStatus.ACTIVE) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_STATUS_NOT_ACTIVE);
                        //return response;
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
                        return new DataProcess<>(resultResponse);
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
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM INFO DELETE BY SESSION ID => [{}]", sessionId);
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, userId);

                ResultResponse resultResponse = new ResultResponse();
                resultResponse.setResult(false);
                //ApiResponse<ResultResponse> response = new ApiResponse<>(resultResponse);

                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    response.setData(response.getData());
                    return response;*/
                } else if(member == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    response.setData(response.getData());
                    return response;*/
                } else {
                    if(member.getMemberStatus().equals(MemberStatus.LOAD)) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED);
                        /*response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_STATUS_LOADED);
                        response.setData(response.getData());
                        return response;*/
                    }
                    sessionService.removeRoom(room);
                    resultResponse.setResult(true);
                    return new DataProcess<>(resultResponse);
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomProfileUpdateResponse> updateRoom(
            String workspaceId,
            String sessionId,
            RoomProfileUpdateRequest roomProfileUpdateRequest) {
        return new RepoDecoder<Room, RoomProfileUpdateResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomProfileUpdateResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE PROFILE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                RoomProfileUpdateResponse profileUpdateResponse = new RoomProfileUpdateResponse();
                String profile = Default.ROOM_PROFILE.getValue();

                Room room = loadFromDatabase();
                if(room != null) {
                    if(room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
                        if (roomProfileUpdateRequest.getProfile() != null) {
                            try {
                                profile = localFileUploadService.upload(roomProfileUpdateRequest.getProfile());
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                        }
                        profileUpdateResponse.setSessionId(sessionId);
                        profileUpdateResponse.setProfile(profile);
                        sessionService.updateRoom(room, profile);
                        return new DataProcess<>(profileUpdateResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
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
                for (Member member : room.getMembers()) {
                    if(member.getUuid().equals(userId)) {
                        log.info("Room has member Id is {}", member.getUuid());
                        if(member.getMemberStatus().equals(MemberStatus.LOAD)) {
                            return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                        } else {
                            return new DataProcess<>(true);
                        }
                    }
                }
                return new DataProcess<>(false, ErrorCode.ERR_ROOM_MEMBER_NOT_ASSIGNED);
                //return new DataProcess<>(true);
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
                for (String coturnUrl : config.remoteServiceProperties.getCoturnUrisList()) {
                    CoturnResponse coturnResponse = new CoturnResponse();
                    coturnResponse.setUsername(config.remoteServiceProperties.getCoturnUsername());
                    coturnResponse.setCredential(config.remoteServiceProperties.getCoturnCredential());
                    coturnResponse.setUrl(coturnUrl);
                    roomResponse.getCoturn().add(coturnResponse);
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

    public DataProcess<Boolean> joinSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
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
                return new DataProcess<>(true);
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
            DataProcess<ResultResponse> invokeDataProcess() {
                Room room = sessionService.getRoom(workspaceId, sessionId);
                Member member = sessionService.getMember(workspaceId, sessionId, userId);

                ResultResponse resultResponse = new ResultResponse();
                resultResponse.setResult(false);
                //ApiResponse<ResultResponse> response = new ApiResponse<>(resultResponse);
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    response.setData(response.getData());
                    return response;*/
                } else if(member == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                    response.setData(response.getData());
                    return response;*/
                } else {
                    if(room.getMembers().isEmpty()) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                        /*response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_INFO_EMPTY);
                        response.setData(response.getData());
                        return response;*/
                    } else {
                        ErrorCode errorCode = sessionService.exitRoom(room, member);
                        if(errorCode.equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                        } else {
                            return new DataProcess<>(ErrorCode.ERR_SERVICE_PROCESS);
                            /*response.setErrorResponseData(errorCode);
                            response.setData(response.getData());
                            return response;*/
                        }
                    }
                }
            }
        }.asApiResponse();
    }

    public DataProcess<Boolean> leaveSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
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

                log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                sessionService.leaveSession(sessionId, clientMetaData);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> destroySession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                sessionService.destroySession(sessionId);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public DataProcess<String> evictParticipant(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Member, String>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return sessionService.getMember(workspaceId, sessionId, userId);
            }

            @Override
            DataProcess<String> invokeDataProcess() {
                Member member = loadFromDatabase();
                if(member != null) {
                    return new DataProcess<>(member.getConnectionId());
                }
                return new DataProcess<>("");
            }
        }.asResponseData();
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
                        if(sessionService.removeMember(room, kickRoomRequest.getParticipantId()).equals(ErrorCode.ERR_SUCCESS)) {
                            resultResponse.setResult(true);
                            return new DataProcess<>(resultResponse);
                            //resultResponse.setResult(true);
                        } else {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_PROCESS_FAIL);
                        }
                        //response.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
                        //response.setData(response.getData());*/
                    }
                }
                //return new DataProcess<>(resultResponse);
                //return response;
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> inviteMember(String workspaceId, String sessionId, InviteRoomRequest inviteRoomRequest) {
        return new RepoDecoder<Room, ResultResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM INVITE MEMBER UPDATE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                Room room = loadFromDatabase();
                ResultResponse resultResponse = new ResultResponse();
                resultResponse.setResult(false);
                if(room != null) {
                    if(room.getLeaderId().equals(inviteRoomRequest.getLeaderId())) {
                        if(room.getMembers().size() + inviteRoomRequest.getParticipantIds().size() >= room.getMaxUserCount()) {
                            return new DataProcess<>(resultResponse, ErrorCode.ERR_ROOM_MEMBER_MAX_COUNT);
                        }

                        for(String participant : inviteRoomRequest.getParticipantIds()) {
                            for(Member member: room.getMembers()) {
                                if(participant.equals(member.getUuid())) {
                                    return new DataProcess<>(resultResponse, ErrorCode.ERR_ROOM_MEMBER_ALREADY_JOINED);
                                }
                            }
                        }
                        sessionService.updateRoom(room, inviteRoomRequest);
                        resultResponse.setResult(true);
                        return new DataProcess<>(resultResponse);
                    } else {
                        return new DataProcess<>(resultResponse, ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(resultResponse, ErrorCode.ERR_ROOM_NOT_FOUND);
                }
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
            DataProcess<MemberInfoResponse> invokeDataProcess() {
                Member member = sessionService.getMember(workspaceId, sessionId, userId);
                MemberInfoResponse resultResponse;
                // mapping data
                resultResponse = modelMapper.map(member, MemberInfoResponse.class);
                return new DataProcess<>(resultResponse);
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
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
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
                    return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));

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
                    return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
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
            DataProcess<RoomHistoryDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                //ApiResponse<RoomHistoryDetailInfoResponse> response = new ApiResponse<>();
                RoomHistory roomHistory = loadFromDatabase();
                if(roomHistory == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;*/
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
                    return new DataProcess<>(resultResponse);
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
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE ALL BY USER ID => [{}]", userId);
                ResultResponse resultResponse = new ResultResponse();
                List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(workspaceId, userId);
                historyService.removeRoomHistory(memberHistoryList);
                resultResponse.setResult(true);
                return new DataProcess<>(resultResponse);
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
            DataProcess<ResultResponse> invokeDataProcess() {
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
                return new DataProcess<>(resultResponse);
            }
        }.asApiResponse();
    }






    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}
